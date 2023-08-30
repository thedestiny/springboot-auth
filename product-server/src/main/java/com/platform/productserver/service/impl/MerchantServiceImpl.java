package com.platform.productserver.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.platform.authcommon.common.AccountTypeEnum;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.common.ResultCode;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.AccountDto;
import com.platform.productserver.dto.BusinessDto;
import com.platform.productserver.dto.MerchantDto;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.Merchant;
import com.platform.productserver.entity.User;
import com.platform.productserver.mapper.*;
import com.platform.productserver.service.MerchantService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 商户账户表 服务实现类
 */
@Slf4j
@Service
public class MerchantServiceImpl extends ServiceImpl<MerchantMapper, Merchant> implements MerchantService {

    @Autowired
    private MerchantMapper merchantMapper;
    @Autowired
    private MerchantLogMapper merchantLogMapper;
    @Autowired
    private FreezeMapper freezeMapper;
    @Autowired
    private FreezeLogMapper freezeLogMapper;
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
    public boolean openAccount(MerchantDto dto) {
        Integer accountType = dto.getAccountType();
        // 检查账户类型
        AccountTypeEnum anEnum = AccountTypeEnum.checkAccountType(accountType);

        User user = userMapper.selectByUserId(dto.getMerchantNo());
        if (ObjectUtil.isEmpty(user)) {
            throw new AppException(ResultCode.NOT_EXIST, "商户信息不存在");
        }
        // 构建商户信息
        Merchant merchant = buildMerchantInfo(dto, accountType, anEnum);

        Object obj = template.execute(status -> {
            try {
                int insert = merchantMapper.insert(merchant);
                return SqlHelper.retBool(insert);
            } catch (Exception e) {
                log.error("商户账户开户异常 {} error", JSONObject.toJSONString(merchant), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "保存账户数据失败！");
        }

        return (Boolean) obj;

    }

    private Merchant buildMerchantInfo(MerchantDto dto, Integer accountType, AccountTypeEnum anEnum) {
        Merchant merchant = new Merchant();
        merchant.setId(IdGenUtils.pid());
        merchant.setAccNo(genAccNo(anEnum.getPrefix()));
        merchant.setSource(dto.getSource());
        merchant.setMerchantNo(dto.getMerchantNo());
        merchant.setStatus(1);
        merchant.setAccountType(accountType);
        merchant.setProdType(dto.getProdType());
        merchant.setBalance(new BigDecimal("0"));
        merchant.setFreezeAmount(new BigDecimal("0"));
        merchant.setIncomeAmount(new BigDecimal("0"));
        merchant.setExpenseAmount(new BigDecimal("0"));
        merchant.setApplyAmount(new BigDecimal("0"));
        merchant.setReversalAmount(new BigDecimal("0"));
        merchant.setBackAmount(new BigDecimal("0"));
        merchant.setSettleAmount(BigDecimal.ZERO);
        merchant.setSeq(Constant.DEFAULT_SEQ);
        return merchant;
    }

    @Override
    public boolean trade(BusinessDto trade) {





        return false;
    }


}
