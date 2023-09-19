package com.platform.productserver.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.google.common.collect.Lists;
import com.platform.authcommon.common.*;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.*;
import com.platform.productserver.entity.*;
import com.platform.productserver.mapper.*;
import com.platform.productserver.service.MerchantService;
import com.platform.productserver.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;


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


    @Override
    public boolean freeze(FreezeDto freezeDto) {

        Merchant merchant = merchantMapper.queryMerchantByNo(freezeDto.getAccNo());
        // 校验商户信息
        checkMerchantInfo(merchant);
        BigDecimal amount = freezeDto.getAmount(); // 冻结金额 和冻结类型
        String activityType = freezeDto.getActivityType();
        FreezeLog transLog = buildFreezeLog(merchant, freezeDto, TransTypeEnum.FREEZE.getCode());     // 构建流水日志
        Object obj = template.execute(status -> {
            try {
                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                // 可用余额
                BigDecimal available = NumberUtil.sub(update.getBalance(), update.getFreezeAmount(), update.getCreditAmount());
                // 冻结金额需要小于可用余额
                if (!NumberUtil.isGreaterOrEqual(amount, available)) {
                    throw new AppException(ResultCode.NOT_EXIST, "商户冻结金额不足!");
                }
                // 设置用户冻结金额
                update.setFreezeAmount(NumberUtil.add(update.getFreezeAmount(), amount));
                merchantMapper.updateById(update);
                // 查询冻结账户
                Freeze freeze = freezeMapper.queryFreezeAccount(update.getId(), activityType);
                BigDecimal freezeAmt = amount;
                if (ObjectUtil.isNotEmpty(freeze)) {
                    freezeAmt = NumberUtil.add(freezeAmt, freeze.getFreezeAmount());
                    freeze.setFreezeAmount(freezeAmt);
                    freezeMapper.updateById(freeze);
                    transLog.setAccountId(freeze.getId());
                } else {
                    Freeze freez = new Freeze();
                    freez.setId(IdGenUtils.pid());
                    freez.setFreezeType(activityType);
                    freez.setAccountId(merchant.getId());
                    freez.setFreezeAmount(amount);
                    freezeMapper.insert(freez);
                    transLog.setAccountId(freez.getId());
                }
                freezeLogMapper.insert(transLog);
                return true;
            } catch (Exception e) {
                log.error("冻结账户交易失败 {} log {} error", JSONObject.toJSONString(freezeDto), JSONObject.toJSONString(transLog), e);
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
    public boolean unFreeze(FreezeDto freezeDto) {

        Merchant merchant = merchantMapper.queryMerchantByNo(freezeDto.getAccNo());
        // 校验商户信息
        checkMerchantInfo(merchant);
        // 冻结金额 和冻结类型
        BigDecimal amount = freezeDto.getAmount();
        String activityType = freezeDto.getActivityType();
        // 构建流水日志
        FreezeLog transLog = buildFreezeLog(merchant, freezeDto, TransTypeEnum.UNFREEZE.getCode());

        Object obj = template.execute(status -> {
            try {

                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                // 查询冻结账户
                Freeze freeze = freezeMapper.queryFreezeAccount(update.getId(), activityType);
                if (ObjectUtil.isEmpty(freeze)) {
                    throw new AppException(ResultCode.NOT_EXIST, "冻结类型不存在!");
                }
                // 查询原冻结流水
                FreezeLog freezeLog = freezeLogMapper.queryFreezeLog(freezeDto.getRequestNo());
                if (ObjectUtil.isEmpty(freezeLog)) {
                    throw new AppException(ResultCode.NOT_EXIST, "原冻结流水不存在!");
                }
                // 冻结类型中的冻结金额
                BigDecimal freezeAmount = freeze.getFreezeAmount();
                if (!NumberUtil.isGreaterOrEqual(freezeAmount, amount)) {
                    throw new AppException(ResultCode.NOT_EXIST, "冻结金额不足!");
                }
                // 设置用户冻结金额
                update.setFreezeAmount(NumberUtil.sub(update.getFreezeAmount(), amount));
                merchantMapper.updateById(update);
                // 解冻冻结账户资金
                freeze.setFreezeAmount(NumberUtil.sub(freezeAmount, amount));
                freezeMapper.updateById(freeze);

                transLog.setAccountId(freeze.getId());
                freezeLogMapper.insert(transLog);

                return true;
            } catch (Exception e) {
                log.error("解冻账户交易失败 {} log {} error", JSONObject.toJSONString(freezeDto), JSONObject.toJSONString(transLog), e);
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
    public boolean freezeIn(FreezeTradeDto freezeDto) {

        Merchant merchant = merchantMapper.queryMerchantByNo(freezeDto.getAccNo());
        // 校验商户信息
        checkMerchantInfo(merchant);
        BigDecimal amount = freezeDto.getAmount(); // 冻结金额 和冻结类型
        String activityType = freezeDto.getActivityType();
        FreezeLog transLog = buildFreezeLog(merchant, freezeDto, TransTypeEnum.FREEZE_IN.getCode());     // 构建流水日志
        MerchantLog merchantLog = buildMerchantLog(freezeDto, merchant, TransTypeEnum.FREEZE_IN.getCode());
        Object obj = template.execute(status -> {
            try {
                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                // 设置用户冻结金额和可用余额
                update.setFreezeAmount(NumberUtil.add(update.getFreezeAmount(), amount));
                update.setBalance(NumberUtil.add(update.getBalance(), amount));
                merchantMapper.updateById(update);
                merchantLog.setBalance(update.getBalance());
                merchantLogMapper.insert(merchantLog);
                // 查询冻结账户
                Freeze freeze = freezeMapper.queryFreezeAccount(update.getId(), activityType);
                BigDecimal freezeAmt = amount;
                if (ObjectUtil.isNotEmpty(freeze)) {
                    freezeAmt = NumberUtil.add(freezeAmt, freeze.getFreezeAmount());
                    freeze.setFreezeAmount(freezeAmt);
                    freezeMapper.updateById(freeze);
                    transLog.setAccountId(freeze.getId());
                } else {
                    Freeze freez = new Freeze();
                    freez.setId(IdGenUtils.pid());
                    freez.setAccountId(merchant.getId());
                    freez.setFreezeType(activityType);
                    freez.setFreezeAmount(amount);
                    freezeMapper.insert(freez);
                    transLog.setAccountId(freez.getId());
                }
                freezeLogMapper.insert(transLog);
                return true;
            } catch (Exception e) {
                log.error("入账冻结操作失败 {} log {} error", JSONObject.toJSONString(freezeDto), JSONObject.toJSONString(transLog), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "操作账户数据失败！");
        }

        return (Boolean) obj;
    }

    /**
     * 可能冻结一次，多次解冻
     *
     * @param freezeDto
     * @return
     */
    @Override
    public boolean unFreezeOut(FreezeTradeDto freezeDto) {

        Merchant merchant = merchantMapper.queryMerchantByNo(freezeDto.getAccNo());
        // 校验商户信息
        checkMerchantInfo(merchant);
        // 获取解冻金额和解冻类型
        BigDecimal amount = freezeDto.getAmount();
        String activityType = freezeDto.getActivityType();
        // 构建流水日志
        FreezeLog transLog = buildFreezeLog(merchant, freezeDto, TransTypeEnum.FREEZE_OUT.getCode());
        MerchantLog merchantLog = buildMerchantLog(freezeDto, merchant, TransTypeEnum.FREEZE_OUT.getCode());
        Object obj = template.execute(status -> {
            try {
                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                // 这里不需要判断金额是否足够，冻结成功即锁定资金，一定可以解冻成功，账户金额足够
                update.setBalance(NumberUtil.sub(update.getBalance(), amount));
                update.setFreezeAmount(NumberUtil.sub(update.getFreezeAmount(), amount));
                merchantLog.setBalance(update.getBalance());

                // 查询冻结账户
                Freeze freeze = freezeMapper.queryFreezeAccount(update.getId(), activityType);
                if (ObjectUtil.isEmpty(freeze)) {
                    throw new AppException(ResultCode.NOT_EXIST, "冻结类型不存在!");
                }
                // 查询原冻结流水
                FreezeLog freezeLog = freezeLogMapper.queryFreezeLog(freezeDto.getRequestNo());
                if (ObjectUtil.isEmpty(freezeLog)) {
                    throw new AppException(ResultCode.NOT_EXIST, "原冻结流水不存在!");
                }
                // 冻结类型中的冻结金额
                BigDecimal freezeAmount = freeze.getFreezeAmount();
                if (!NumberUtil.isGreaterOrEqual(freezeAmount, amount)) {
                    throw new AppException(ResultCode.NOT_EXIST, "冻结金额不足!");
                }
                // 设置用户冻结金额, 原冻结金额 - 解冻金额
                update.setFreezeAmount(NumberUtil.sub(update.getFreezeAmount(), amount));
                merchantMapper.updateById(update);
                // 解冻冻结账户资金
                freeze.setFreezeAmount(NumberUtil.sub(freezeAmount, amount));
                freezeMapper.updateById(freeze);

                transLog.setAccountId(freeze.getId());
                freezeLogMapper.insert(transLog);
                // 更新账户余额并保存账户流水记录
                merchantMapper.updateById(update);
                merchantLogMapper.insert(merchantLog);

                return true;
            } catch (Exception e) {
                log.error("解冻账户交易失败 {} log {} error", JSONObject.toJSONString(freezeDto), JSONObject.toJSONString(transLog), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "操作账户数据失败!");
        }

        return (Boolean) obj;
    }

    @Override
    public boolean batchTradeIn(BatchTradeDto tradeDto) {

        Merchant merchant = merchantMapper.queryMerchantInfo(tradeDto.getMerchantNo(), tradeDto.getAccountType());
        checkMerchantInfo(merchant);

        List<BatchUserTradeDto> tradeList = tradeDto.getTradeList();
        if (CollUtil.isEmpty(tradeList)) {
            throw new AppException(ResultCode.NOT_EXIST, "交易列表信息不存在!");
        }
        // 交易总金额
        BigDecimal reduce = tradeList.stream().map(BatchUserTradeDto::getAmount).reduce(BigDecimal.ZERO, NumberUtil::add);
        if (!NumberUtil.equals(tradeDto.getAmount(), reduce)) {
            throw new AppException(ResultCode.VALIDATE_FAILED, "交易金额不一致!");
        }
        // 交易单号不能重复
        int size = CollUtil.size(tradeList);
        long count = tradeList.stream().map(BatchUserTradeDto::getRequestNo).distinct().count();
        if (size - count != 0) {
            throw new AppException(ResultCode.VALIDATE_FAILED, "存在相同的交易单号!");
        }
        Boolean credit = tradeDto.getCredit();
        BigDecimal amount = tradeDto.getAmount();

        List<MerchantLog> logList = buildMerchantLogList(tradeList, merchant, TransTypeEnum.TRADE_IN.getCode());

        Object obj = template.execute(status -> {
            try {

                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                // 查询账户余额
                BigDecimal original = update.getBalance();
                AppUtils.opt(update, amount, TransTypeEnum.TRADE_IN.getOpt(), credit);
                for (MerchantLog merchantLog : logList) {
                    // 设置账户交易后余额
                    merchantLog.setBalance(NumberUtil.add(original, merchantLog.getTransAmount()));
                }
                // 记录交易记录 更新交易流水表
                merchantLogMapper.insertEntityList(logList);
                merchantMapper.updateById(update);
                return true;
            } catch (Exception e) {
                log.error("账户批量入账交易失败 {} error", JSONObject.toJSONString(tradeDto), e);
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
    public boolean batchTradeOut(BatchTradeDto tradeDto) {

        Merchant merchant = merchantMapper.queryMerchantInfo(tradeDto.getMerchantNo(), tradeDto.getAccountType());
        checkMerchantInfo(merchant);

        List<BatchUserTradeDto> tradeList = tradeDto.getTradeList();
        if (CollUtil.isEmpty(tradeList)) {
            throw new AppException(ResultCode.NOT_EXIST, "交易列表信息不存在!");
        }
        // 交易总金额
        BigDecimal reduce = tradeList.stream().map(BatchUserTradeDto::getAmount).reduce(BigDecimal.ZERO, NumberUtil::add);
        if (!NumberUtil.equals(tradeDto.getAmount(), reduce)) {
            throw new AppException(ResultCode.VALIDATE_FAILED, "交易金额不一致!");
        }
        // 交易单号不能重复
        int size = CollUtil.size(tradeList);
        long count = tradeList.stream().map(BatchUserTradeDto::getRequestNo).distinct().count();
        if (size - count != 0) {
            throw new AppException(ResultCode.VALIDATE_FAILED, "存在相同的交易单号!");
        }
        Boolean credit = tradeDto.getCredit();
        BigDecimal amount = tradeDto.getAmount();

        List<MerchantLog> logList = buildMerchantLogList(tradeList, merchant, TransTypeEnum.TRADE_OUT.getCode());

        Object obj = template.execute(status -> {
            try {

                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                // 查询账户余额
                BigDecimal original = update.getBalance();
                AppUtils.opt(update, amount, TransTypeEnum.TRADE_OUT.getOpt(), credit);
                for (MerchantLog merchantLog : logList) {
                    // 设置账户交易后余额
                    merchantLog.setBalance(NumberUtil.sub(original, merchantLog.getTransAmount()));
                }
                // 记录交易记录 更新交易流水表
                merchantLogMapper.insertEntityList(logList);
                merchantMapper.updateById(update);
                return true;
            } catch (Exception e) {
                log.error("账户批量出账交易失败 {} error", JSONObject.toJSONString(tradeDto), e);
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


    @Override
    public boolean trade(BusinessDto trade) {
        // 校验交易类型
        TransTypeEnum transTypeEnum = TransTypeEnum.checkTransType(trade.getTransType());

        Merchant merchant = merchantMapper.queryMerchantInfo(trade.getMerchantNo(), trade.getAccountType());
        if (ObjectUtil.isNull(merchant)) {
            throw new AppException(ResultCode.NOT_EXIST);
        }
        // 交易类型和交易金额 是否允许欠款
        Integer transType = trade.getTransType();
        BigDecimal amount = trade.getAmount();
        Boolean credit = trade.getCredit();

        // 构建流水日志
        MerchantLog transLog = buildMerchantLog(trade, merchant, transType, amount);

        Object obj = template.execute(status -> {
            try {

                Merchant update = merchantMapper.queryMerchantForUpdate(merchant.getId());
                AppUtils.opt(update, amount, transTypeEnum.getOpt(), credit);
                transLog.setBalance(update.getBalance());
                // 记录交易记录 更新交易流水表
                merchantLogMapper.insert(transLog);
                merchantMapper.updateById(update);
                return true;
            } catch (Exception e) {
                log.error("账户交易失败 {} log {} error", JSONObject.toJSONString(trade), JSONObject.toJSONString(transLog), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "操作账户数据失败！");
        }

        return (Boolean) obj;

    }

    private MerchantLog buildMerchantLog(BusinessDto trade, Merchant merchant, Integer transType, BigDecimal amount) {
        MerchantLog transLog = new MerchantLog();
        transLog.setId(IdGenUtils.pid());
        transLog.setAccountId(merchant.getId());
        transLog.setMerchantNo(merchant.getMerchantNo());
        transLog.setAccNo(merchant.getAccNo());
        transLog.setAccountType(merchant.getAccountType());
        transLog.setRequestNo(trade.getRequestNo());
        transLog.setOrderNo(trade.getOrderNo());
        transLog.setOtherAccount(trade.getOtherAccount());
        transLog.setOtherAccountType(trade.getOtherAccountType());
        transLog.setActionType(transType);
        transLog.setProdType(trade.getProdType());
        transLog.setTransAmount(amount);
        transLog.setRemark(trade.getRemark());
        transLog.setSource(trade.getSource());
        transLog.setAppId(trade.getAppId());
        return transLog;
    }

    private MerchantLog buildMerchantLog(FreezeTradeDto freezeDto, Merchant merchant, Integer transType) {

        MerchantLog transLog = new MerchantLog();
        transLog.setId(IdGenUtils.pid());
        transLog.setAccountId(merchant.getId());
        transLog.setMerchantNo(merchant.getMerchantNo());
        transLog.setAccNo(freezeDto.getAccNo());
        transLog.setAccountType(merchant.getAccountType());
        transLog.setRequestNo(freezeDto.getRequestNo());
        transLog.setOrderNo(freezeDto.getOrderNo());
        transLog.setOtherAccount(freezeDto.getOtherAccount());
        transLog.setOtherAccountType(freezeDto.getOtherAccountType());
        transLog.setActionType(transType);
        transLog.setProdType(freezeDto.getProdType());
        transLog.setTransAmount(freezeDto.getAmount());
        transLog.setRemark(freezeDto.getRemark());
        transLog.setSource(freezeDto.getSource());
        transLog.setAppId(freezeDto.getAppId());
        return transLog;
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

    private String genAccNo(String prefix) {
        String time = DateUtil.format(DateUtil.date(), "yyMMdd");
        long increment = redisClient.increment(time);
        String format = String.format("%05d", increment);
        return StrUtil.format("{}-{}-{}", prefix, time, format);
    }

    private FreezeLog buildFreezeLog(Merchant merchant, FreezeDto freezeDto, Integer actionType) {

        FreezeLog log1 = new FreezeLog();
        log1.setAccountId(merchant.getId());
        log1.setActionType(actionType);
        log1.setAccNo(merchant.getAccNo());
        log1.setFreezeType(freezeDto.getActivityType());
        log1.setFreezeAmount(freezeDto.getAmount());
        log1.setRequestNo(freezeDto.getRequestNo());
        log1.setOrderNo(freezeDto.getOrderNo());
        log1.setProdType(freezeDto.getProdType());
        log1.setAppId(freezeDto.getAppId());
        log1.setSource(freezeDto.getSource());
        log1.setRemark(freezeDto.getRemark());
        return log1;
    }

    private void checkMerchantInfo(Merchant merchant) {

        // 判断商户状态
        if (ObjectUtil.isEmpty(merchant)) {
            throw new AppException(ResultCode.NOT_EXIST, "商户信息不存在!");
        }
        if (!NumberUtil.equals(merchant.getStatus(), StatusEnum.ENABLE.code)) {
            throw new AppException(ResultCode.NOT_EXIST, "商户状态未启用!");
        }

    }

    private List<MerchantLog> buildMerchantLogList(List<BatchUserTradeDto> tradeList, Merchant merchant, Integer actionType) {

        List<MerchantLog> objects = Lists.newArrayList();

        for (BatchUserTradeDto dto : tradeList) {
            MerchantLog node = new MerchantLog();
            node.setAccountId(merchant.getId());
            node.setMerchantNo(merchant.getMerchantNo());
            node.setAccNo(merchant.getAccNo());
            node.setAccountType(merchant.getAccountType());
            node.setRequestNo(dto.getRequestNo());
            node.setOrderNo(dto.getOrderNo());
            node.setOtherAccount(dto.getOtherAccount());
            node.setOtherAccountType(dto.getOtherAccountType());
            node.setActionType(actionType);
            node.setProdType(dto.getProdType());
            node.setTransAmount(dto.getAmount());
            // node.setBalance(new BigDecimal("0"));
            node.setRemark(dto.getRemark());
            node.setSource(dto.getSource());
            node.setAppId(dto.getAppId());
            objects.add(node);
        }


        return objects;

    }


}
