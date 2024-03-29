package com.platform.productserver.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.platform.authcommon.common.*;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.productserver.dto.BaseNode;
import com.platform.productserver.dto.BatchTradeResp;
import com.platform.productserver.utils.AppUtils;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.AccountDto;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.Account;
import com.platform.productserver.entity.AccountLog;
import com.platform.productserver.entity.User;
import com.platform.productserver.mapper.AccountLogMapper;
import com.platform.productserver.mapper.AccountMapper;
import com.platform.productserver.mapper.UserMapper;
import com.platform.productserver.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户账户表 服务实现类
 *
 * @since 2023-08-20
 */

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountLogMapper logMapper;
    @Autowired
    private TransactionTemplate template;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisUtils redisClient;

    public String genAccNo(String prefix) {
        String time = DateUtil.format(DateUtil.date(), "yyMMdd");
        long increment = redisClient.increment(time);
        String format = String.format("%05d", increment);
        return StrUtil.format("{}-{}-{}", prefix, time, format);
    }

    @Override
    public boolean openAccount(AccountDto dto) {

        Integer accountType = dto.getAccountType();
        // 检查账户类型 10-内部账户 11-外部账户 12-管理者
        AccountTypeEnum anEnum = AccountTypeEnum.checkAccountType(accountType);
        User user = userMapper.selectByUserId(dto.getUserId());
        if (ObjectUtil.isEmpty(user)) {
            throw new AppException(ResultCode.NOT_EXIST, "用户信息不存在");
        }
        // 初始化账户数据
        Account account = new Account();
        account.setId(IdGenUtils.pid());
        account.setUserId(dto.getUserId());
        account.setAccNo(genAccNo(anEnum.getPrefix()));
        account.setAccountType(dto.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(1);
        account.setIncomeAmount(BigDecimal.ZERO);
        account.setExpenseAmount(BigDecimal.ZERO);
        account.setCreditAmount(BigDecimal.ZERO);
        account.setSeq(Constant.DEFAULT_SEQ);

        Object obj = template.execute(status -> {
            try {
                int insert = accountMapper.insert(account);
                return SqlHelper.retBool(insert);
            } catch (Exception e) {
                log.error("账户开户异常 {} error", JSONObject.toJSONString(account), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "保存账户数据失败！");
        }
        return (Boolean) obj;
    }

    @Override
    public boolean trade(TradeDto tradeDto) {

        // 查询账户信息
        Account account = accountMapper.queryAccount(tradeDto.getUserId(), tradeDto.getAccountType());
        if (ObjectUtil.isNull(account)) {
            throw new AppException(ResultCode.NOT_EXIST);
        }
        // 交易类型和交易金额 是否允许欠款
        Integer transType = tradeDto.getTransType();
        BigDecimal amount = tradeDto.getAmount();
        Boolean credit = tradeDto.getCredit();
        // 交易类型
        TransTypeEnum transTypeEnum = TransTypeEnum.checkTransType(transType);
        // 初始化账户流水数据
        AccountLog accountLog = new AccountLog();
        accountLog.setAccountId(account.getId());
        accountLog.setRequestNo(tradeDto.getRequestNo());
        accountLog.setOrderNo(tradeDto.getOrderNo());
        accountLog.setAccNo(account.getAccNo());
        accountLog.setUserId(account.getUserId());
        accountLog.setAccountType(account.getAccountType());
        accountLog.setOtherAccount(tradeDto.getOtherAccount());
        accountLog.setOtherAccountType(tradeDto.getOtherAccountType());
        accountLog.setActionType(transType);
        accountLog.setProdType(tradeDto.getProdType());
        accountLog.setTransAmount(amount);
        accountLog.setSource(tradeDto.getSource());
        accountLog.setAppId(tradeDto.getAppId());
        accountLog.setRemark(tradeDto.getRemark());
        accountLog.setSeq(Constant.DEFAULT_SEQ);
        // 账户事务操作
        Object obj = template.execute(status -> {
            try {
                Account update = accountMapper.queryAccountForUpdate(account.getId());
                // 计算账户金额
                AppUtils.opt(update, amount, transTypeEnum.getOpt(), credit);
                accountLog.setBalance(update.getBalance());
                // 记录交易记录 更新交易流水表
                logMapper.insert(accountLog);
                accountMapper.updateById(update);
                return true;
            } catch (Exception e) {
                log.error("账户交易失败 {} log {} error", JSONObject.toJSONString(tradeDto), JSONObject.toJSONString(accountLog), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "操作账户数据失败！");
        }
        return (Boolean) obj;
    }


    @Override
    public BatchTradeResp tradeBatch(List<TradeDto> dtoList) {

        if (CollUtil.isEmpty(dtoList)) {
            throw new AppException(ResultCode.SAVE_FAILURE, "批量转账操作数据为空！");
        }

        List<BaseNode> dataList = Lists.newArrayList();

        for (TradeDto tradeDto : dtoList) {
            BaseNode node = new BaseNode();
            try {
                boolean trade = trade(tradeDto);
                node.setRequestNo(tradeDto.getRequestNo());
                node.setStatus(trade ? OrderStatusEnum.SUCCESS.getCode() : OrderStatusEnum.FAIL.getCode());
            } catch (Exception e) {
                log.error("error is {} ", e.getMessage(), e);
                node.setRequestNo(tradeDto.getRequestNo());
                node.setStatus(OrderStatusEnum.FAIL.getCode());
                node.setError(e.getMessage());
            }
            dataList.add(node);
        }
        BatchTradeResp resp = new BatchTradeResp();
        resp.setDataList(dataList);
        return resp;
    }
}
