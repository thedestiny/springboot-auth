package com.platform.productserver.business;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.platform.authcommon.common.AccountTypeEnum;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.common.ResultCode;
import com.platform.authcommon.common.TransTypeEnum;
import com.platform.authcommon.common.aop.annotation.DistributedLock;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.PkgInLog;
import com.platform.productserver.entity.PkgOutLog;
import com.platform.productserver.ext.PkgOutLogExt;
import com.platform.productserver.mapper.PkgInLogMapper;
import com.platform.productserver.mapper.PkgOutLogMapper;
import com.platform.productserver.redpkg.ReceivePkgReq;
import com.platform.productserver.redpkg.RedPkgEnum;
import com.platform.productserver.redpkg.RedPkgService;
import com.platform.productserver.redpkg.SendPkgReq;
import com.platform.productserver.service.AccountService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description 红包业务
 * @Date 2023-08-17 9:41 AM
 */
@Service
public class RedPkgBusiness {
    @Autowired
    private Map<String, RedPkgService> redPkgServiceMap;
    @Autowired
    private PkgOutLogMapper outLogMapper;
    @Autowired
    private PkgInLogMapper inLogMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private AccountService accountService;


    /**
     * 红包发送
     *
     * @param req
     */
    @DistributedLock(prefix = "redis-red-send", key = "#req.orderNo")
    public boolean sendRedPkg(SendPkgReq req) {
        // 红包类型 1-个人红包 2-群红包平分模式 2群红包拼手气
        Integer redType = req.getRedType();
        RedPkgEnum redPkgEnum = RedPkgEnum.queryPkgByType(redType);
        if (redPkgEnum == null) {
            throw new AppException("红包类型不存在");
        }
        RedPkgService redPkgService = redPkgServiceMap.get(redPkgEnum.name);
        Boolean redPkg = redPkgService.sendRedPkg(req);
        return redPkg;
    }

    @DistributedLock(prefix = "redis-red-receive", key = "#req.requestNo")
    public boolean receiveRedPkg(ReceivePkgReq req) {

        PkgOutLog pkgOutLog = outLogMapper.selectByOrderNo(req.getOrderNo());
        String prodType = pkgOutLog.getProdType();
        if (!StrUtil.equalsAny(prodType, "100", "101")) {
            throw new AppException("红包类型不存在");
        }
        RedPkgEnum redPkgEnum = RedPkgEnum.queryPkgByCode(prodType);
        RedPkgService redPkgService = redPkgServiceMap.get(redPkgEnum.name);
        Boolean redPkg = redPkgService.receiveRedPkg(req);
        return redPkg;
    }

    /**
     * 红包超时定时任务
     */
    public void handleRedPkgOverTime() {

        PkgOutLogExt ext = new PkgOutLogExt();
        ext.setFetchSize(Constant.BATCH_SIZE);
        ext.setStartId(0L);
        while (true) {
            List<PkgOutLog> logs = outLogMapper.queryPkgOutTimeList(ext);   // 查询超时未领取的红包
            if (CollUtil.isEmpty(logs)) {return;}
            ext.setStartId(logs.get(logs.size() - 1).getId()); // 设置下次查询的id
            for (PkgOutLog log : logs) {
                // 删除 redis 中红包信息
                String key = Constant.RED_PKG_PREFIX + log.getOrderNo();
                redisUtils.delete(key);
                PkgInLog query = new PkgInLog();
                query.setFid(log.getId());
                List<PkgInLog> pkgInLogs = inLogMapper.queryPkgInLogList(query);
                BigDecimal receiveAmt = BigDecimal.ZERO;  // 红包已领取金额
                if (CollUtil.isNotEmpty(pkgInLogs)) {
                    receiveAmt = pkgInLogs.stream().map(PkgInLog::getAmount).reduce(BigDecimal.ZERO, NumberUtil::add);
                }
                // 红包需要退回金额
                BigDecimal refund = NumberUtil.sub(log.getAmount(), receiveAmt);
                if (NumberUtil.isGreater(refund, BigDecimal.ZERO)) {
                    PkgInLog inLog = buildPkgInLog(log, refund);   // 创建红包退回记录
                    int insert = inLogMapper.insert(inLog); // 保存红包领取记录
                    tradeAccountIn(log, refund, inLog);
                    // 插入数据成功后，调用
                    inLog.setStatus(1);
                    inLogMapper.updateById(inLog);
                }
                log.setReceiveAmount(receiveAmt);
                log.setRefundAmount(refund);
                // 标记该红包已经处理过
                log.setFlag(1);
                // 更新红包发送表的状态
                outLogMapper.updateById(log);
            }
        }
    }

    private void tradeAccountIn(PkgOutLog log, BigDecimal refund, PkgInLog inLog) {
        // 调用 C 端入账接口
        TradeDto tradeDto = new TradeDto();
        tradeDto.setUserId(log.getUserId());
        tradeDto.setAccountType(AccountTypeEnum.INNER.getCode());
        tradeDto.setAmount(refund);
        tradeDto.setRequestNo(inLog.getRequestNo());
        tradeDto.setOrderNo(log.getOrderNo());
        tradeDto.setOtherAccount(log.getUserId());
        tradeDto.setOtherAccountType(AccountTypeEnum.INNER.getCode());
        tradeDto.setProdType(log.getProdType());
        tradeDto.setTransType(TransTypeEnum.TRANS_IN.getCode());
        tradeDto.setSource(log.getSource());
        tradeDto.setRemark(inLog.getRemark());
        tradeDto.setAppId(log.getAppId());
        // 调用C端出账接口
        boolean trade = accountService.trade(tradeDto);
        if (!trade) {
            throw new AppException(ResultCode.FAILED, "红包领取失败");
        }
    }

    @NotNull
    private PkgInLog buildPkgInLog(PkgOutLog log, BigDecimal refund) {
        PkgInLog inLog = new PkgInLog();
        inLog.setFid(log.getId());
        inLog.setSource(log.getSource());
        inLog.setRequestNo(IdGenUtils.orderNo());
        inLog.setOrderNo(log.getOrderNo());
        inLog.setUserId(log.getUserId());
        inLog.setAmount(refund);
        inLog.setStatus(0);
        inLog.setErrorMsg("红包超时退回");
        inLog.setProdType(log.getProdType());
        inLog.setActionType(1);
        inLog.setRemark("红包超时退回");
        return inLog;
    }


}
