package com.platform.productserver.grant;
import java.math.BigDecimal;


import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.platform.authcommon.common.OrderStatusEnum;
import com.platform.authcommon.common.ResultCode;
import com.platform.authcommon.common.TransTypeEnum;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.BatchTradeDto;
import com.platform.productserver.dto.BatchTradeResp;
import com.platform.productserver.dto.BatchUserTradeDto;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.BtransLog;
import com.platform.productserver.entity.CtransLog;
import com.platform.productserver.entity.GiveBatchInfo;
import com.platform.productserver.entity.GiveLog;
import com.platform.productserver.service.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 积分分发
 *
 * @Description
 * @Date 2023-08-30 2:29 PM
 */

@Slf4j
@Service
public class GrantBusiness {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TransactionTemplate template;
    @Autowired
    private GiveBatchInfoService batchInfoService;
    @Autowired
    private GiveLogService giveLogService;
    @Autowired
    private GiveRefundLogService refundLogService;
    @Autowired
    private TransLogService transLogService;

    /**
     * C 端和 B 端 账户服务
     */
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private AccountService accountService;


    /**
     * 单笔积分分发
     *
     * @param account
     * @return
     */
    public GiveResp point(GiveReq account) {

        GiveResp resp = new GiveResp();


        return resp;
    }

    public GiveResp pointBatch(BatchGiveReq batchReq) {
        GiveResp resp = new GiveResp();
        // 查询红包批次信息
        String batchNo = batchReq.getBatchNo();
        GiveBatchInfo batchInfo = batchInfoService.queryBatchInfo(batchNo);
        // 分发批次信息已经存在，进行校验即可
        if (ObjectUtil.isNotEmpty(batchInfo)) {
            Integer status = batchInfo.getStatus();
            // 处理成功或者失败的情况
            if (OrderStatusEnum.SUCCESS.getCode().equals(status) || OrderStatusEnum.FAIL.getCode().equals(status)) {
                resp.setOrderNo(batchReq.getOrderNo());
                resp.setRequestNo(batchReq.getRequestNo());
                resp.setStatus(batchInfo.getStatus());
                resp.setBatchNo(batchInfo.getBatchNo());
                return resp;
            }
            // 幂等，处理中的数据需要继续处理
            if (OrderStatusEnum.PROCESSING.getCode().equals(status)) {
                retryGrant(batchInfo, resp);
                return resp;
            }
        }
        List<GiveUserDto> userList = batchReq.getUserList();
        if (CollUtil.isEmpty(userList)) {
            throw new AppException(ResultCode.NOT_EXIST, "发放列表数据不存在!");
        }
        GrantContext ctx = new GrantContext();
        // 1 保存业务记录信息
        saveGrantInfo(batchReq, userList, ctx);
        // 2 执行积分分发
        boolean result = executeGrantInfo(ctx);
        // 3 执行分发成功后，修改分发批次表和分发日志表
        if(result){
            GiveBatchInfo batchInf = ctx.getBatchInf();
            List<GiveLog> logs = ctx.getLogs();
            Object obj = template.execute(status -> {
                try {
                    Integer ef1 = batchInfoService.updateBatchInfo(batchInf);
                    Integer ef2 = giveLogService.updateGiveLogList(logs);
                    return true;
                } catch (Exception e) {
                    log.error("分发数据修改异常 {} error", JSONObject.toJSONString(batchReq), e);
                    status.setRollbackOnly();
                    throw e;
                }
            });
            if (obj instanceof Exception) {
                throw new AppException(ResultCode.SAVE_FAILURE, "修改分发数据失败！");
            }
        }
        return resp;
    }



    /**
     * 执行发放动作
     */
    private boolean executeGrantInfo(GrantContext ctx) {
        // 数据保存成功方可发送
        if (Boolean.FALSE.equals(ctx.getSaveFlag())) {
            return false;
        }

        GiveBatchInfo batchInf = ctx.getBatchInf();
        BatchGiveReq batchReq = ctx.getBatchReq();

        // 1 调用批量接口先处理 B 端出账接口
        List<BtransLog> btransLogs = ctx.getBtransLogs();
        boolean bFlag = false;
        if(CollUtil.isNotEmpty(btransLogs)){
            // 构建B端请求参数&调用B端批量出账接口
            BatchTradeDto tradeDto = buildBatchMerchantOut(batchInf, batchReq, btransLogs);
            bFlag = merchantService.batchTradeOut(tradeDto);
            // 修改b 端流水表日志

        }
        boolean cFlag = false;
        // 2 B 端出账成功后，调用 C 端入账接口
        List<CtransLog> ctransLogs = ctx.getCtransLogs();
        if(CollUtil.isNotEmpty(ctransLogs) && bFlag){
            List<TradeDto> dtoList = buildBatchAccountIn(batchReq, ctransLogs);
            BatchTradeResp batchTradeResp = accountService.tradeBatch(dtoList);
            // 修改 c 端流水表日志
            cFlag = true;
        }
        return bFlag && cFlag;
    }

    /**
     * 构建 c 端账户入账参数
     */
    private List<TradeDto> buildBatchAccountIn(BatchGiveReq batchReq, List<CtransLog> ctransLogs) {
        List<TradeDto> dtoList = Lists.newArrayList();
        for (CtransLog ctransLog : ctransLogs) {
            TradeDto dto = new TradeDto();
            dto.setTransId(0L);
            dto.setUserId(ctransLog.getUserId());
            dto.setAccountType(ctransLog.getAccountType());
            dto.setAmount(ctransLog.getAmount());
            dto.setRequestNo(ctransLog.getRequestNo());
            dto.setOrderNo(ctransLog.getRequestNo());
            dto.setOtherAccount(batchReq.getOutAccNo());
            dto.setOtherAccountType(0);
            dto.setProdType(ctransLog.getProdType());
            dto.setTransType(ctransLog.getActionType());
            dto.setSource(ctransLog.getSource());
            dto.setRemark(ctransLog.getRemark());
            dto.setAppId("");
            dtoList.add(dto);
        }
        return dtoList;
    }

    // 构建 B 端出账参数
    private BatchTradeDto buildBatchMerchantOut(GiveBatchInfo batchInf, BatchGiveReq batchReq, List<BtransLog> btransLogs) {
        BatchTradeDto tradeDto = new BatchTradeDto();
        tradeDto.setMerchantNo(batchInf.getMerchantNo());
        tradeDto.setAccountType(batchReq.getOutAccNoType());
        tradeDto.setCredit(false);
        tradeDto.setAmount(batchInf.getAmount());

        List<BatchUserTradeDto> tradeList = Lists.newArrayList();
        for (BtransLog btransLog : btransLogs) {

            BatchUserTradeDto element = new BatchUserTradeDto();
            element.setOtherAccount(btransLog.getOtherAccNo());
            element.setOtherAccountType(0);
            element.setAmount(btransLog.getAmount());
            element.setProdType(btransLog.getProdType());
            element.setTransType(btransLog.getActionType());
            element.setSource(btransLog.getSource());
            element.setRemark(btransLog.getRemark());
            element.setAppId(btransLog.getAppId());
            element.setOrderNo(btransLog.getRequestNo());
            element.setRequestNo(btransLog.getRequestNo());

            tradeList.add(element);
        }
        //
        tradeDto.setTradeList(tradeList);
        return tradeDto;
    }

    private void saveGrantInfo(BatchGiveReq batchReq, List<GiveUserDto> userList, GrantContext ctx) {
        // 构建批次信息
        GiveBatchInfo batchInf = buildGrantBatchInfo(batchReq, userList);
        List<GiveLog> logs = Lists.newArrayList();
        List<BtransLog> blogs = Lists.newArrayList();
        List<CtransLog> clogs = Lists.newArrayList();
        for (GiveUserDto dto : userList) {
            GiveLog node = buildGiveLog(batchReq, batchInf, dto);
            logs.add(node);
            // 构建 B 端 和 C 端的日志记录
            BtransLog btransLog = buildBtransLog(node, batchReq.getOutAccNo(), batchReq.getActivityType(), TransTypeEnum.TRANS_OUT.getCode());
            CtransLog ctransLog = buildCtransLog(node, batchReq, TransTypeEnum.TRANS_IN.getCode());
            blogs.add(btransLog);
            clogs.add(ctransLog);
        }
        Object obj = template.execute(status -> {
            try {
                // 保存分发信息表和日志表、 c 端和 b 端操作日志表
                Integer ef1 = batchInfoService.saveBatchInfo(batchInf);
                Integer ef2 = giveLogService.insertGiveLogList(logs);
                Integer ef3 = transLogService.insertBtransLogs(blogs);
                Integer ef4 = transLogService.insertCtransLogs(clogs);
                return true;
            } catch (Exception e) {
                log.error("分发数据异常 {} error", JSONObject.toJSONString(batchReq), e);
                status.setRollbackOnly();
                throw e;
            }
        });
        if (obj instanceof Exception) {
            throw new AppException(ResultCode.SAVE_FAILURE, "保存分发数据失败！");
        }
        ctx.setSaveFlag(Boolean.FALSE);
        if (Boolean.TRUE.equals((Boolean) obj)) {
            // 设置批次信息 b 端日志 和 c 端日志
            ctx.setSaveFlag(Boolean.TRUE);
            ctx.setBatchReq(batchReq);
            ctx.setBatchInf(batchInf);
            ctx.setLogs(logs);
            ctx.setBtransLogs(blogs);
            ctx.setCtransLogs(clogs);
        }


    }

    private BtransLog buildBtransLog(GiveLog node, String accNo, String activityType, Integer actionType) {
        BtransLog btransLog = new BtransLog();
        btransLog.setSource(node.getSource());
        btransLog.setFid(node.getId());
        btransLog.setRequestNo(node.getRequestNo());
        btransLog.setAccNo(accNo);
        btransLog.setActionType(actionType);
        btransLog.setProdType(node.getProdType());
        btransLog.setAmount(node.getAmount());
        btransLog.setStatus(0);
        btransLog.setErrorMsg("");
        btransLog.setRemark(node.getRemark());
        btransLog.setAppId(node.getAppId());
        btransLog.setExclusiveNo(node.getExclusiveNo());
        btransLog.setActivityType(activityType);
        btransLog.setOtherAccNo("");
        btransLog.setSeq(0L);
        return btransLog;
    }

    private CtransLog buildCtransLog(GiveLog node, BatchGiveReq batchReq, Integer actionType) {
        CtransLog ctransLog = new CtransLog();
        ctransLog.setSource(node.getSource());
        ctransLog.setFid(node.getId());
        ctransLog.setRequestNo(node.getRequestNo());
        ctransLog.setUserId(node.getUserId());
        ctransLog.setAccountType(node.getAccountType());
        ctransLog.setProdType(node.getProdType());
        ctransLog.setActionType(actionType);
        ctransLog.setAmount(node.getAmount());
        ctransLog.setStatus(0);
        ctransLog.setErrorMsg("");
        ctransLog.setRemark(node.getRemark());
        ctransLog.setSeq(0L);
        return ctransLog;
    }

    /**
     * 构建分发日志信息
     */
    private GiveLog buildGiveLog(BatchGiveReq batchReq, GiveBatchInfo batchInf, GiveUserDto dto) {
        GiveLog node = new GiveLog();
        node.setId(IdGenUtils.pid());
        node.setRequestNo(dto.getRequestNo());
        node.setOrderNo(dto.getOrderNo());
        node.setBatchNo(batchInf.getBatchNo());
        node.setAmount(dto.getAmount());
        node.setMerchantNo(batchInf.getMerchantNo());
        node.setOutAccNo(batchInf.getOutAccNo());
        node.setInAccNo("");
        node.setUserId(dto.getUserId());
        node.setAccountType(dto.getAccountType());
        node.setDataType(dto.getDataType());
        node.setGiveType(batchInf.getAsynFlag());
        node.setStatus(0);
        node.setRemark(dto.getRemark());
        node.setFailureMsg("");
        node.setActivityNo(batchInf.getActivityNo());
        node.setProdType(dto.getProdType());
        node.setTradeSummary(dto.getTradeSummary());
        node.setExclusiveNo(batchReq.getExclusiveNo());
        node.setChannel(batchInf.getChannel());
        node.setAppId(batchInf.getAppId());
        node.setSource(batchInf.getSource());
        return node;
    }

    /**
     * 构建分发批次信息
     */
    private GiveBatchInfo buildGrantBatchInfo(BatchGiveReq batchReq, List<GiveUserDto> userList) {
        GiveBatchInfo info = new GiveBatchInfo();
        info.setAppId(batchReq.getAppId());
        info.setBatchNo(batchReq.getBatchNo());
        info.setActivityType(batchReq.getActivityType());
        info.setMerchantNo(batchReq.getMerchantNo());
        info.setSerialNo("");
        info.setGiveCount(CollUtil.size(userList));
        info.setReceiveCount(0);
        info.setStatus(0);
        info.setDealCount(0);
        info.setResultType(batchReq.getResultType());
        info.setAsynFlag(batchReq.getGrantDataType());
        info.setErrorMsg("");
        info.setAmount(batchReq.getAmount());
        info.setChannel(batchReq.getChannel());
        info.setActivityNo(batchReq.getActivityNo());
        info.setOutAccNo(batchReq.getOutAccNo());
        info.setSource(batchReq.getSource());
        info.setCallbackUrl(batchReq.getCallBackUrl());
        info.setCallbackIdentity("");
        info.setExpireTime(batchReq.getExpireTime());
        return info;
    }

    /**
     * 重试发放
     *
     * @param batchInfo
     * @param resp
     */
    private void retryGrant(GiveBatchInfo batchInfo, GiveResp resp) {


    }
}