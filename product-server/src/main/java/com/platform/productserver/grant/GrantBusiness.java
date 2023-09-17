package com.platform.productserver.grant;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import com.platform.authcommon.common.OrderStatusEnum;
import com.platform.authcommon.common.ResultCode;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import com.platform.productserver.entity.GiveBatchInfo;
import com.platform.productserver.entity.GiveLog;
import com.platform.productserver.service.GiveBatchInfoService;
import com.platform.productserver.service.GiveLogService;
import com.platform.productserver.service.GiveRefundLogService;
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
            // todo 幂等，处理中的数据需要继续处理
            if (OrderStatusEnum.PROCESSING.getCode().equals(status)) {
                retryGrant(batchInfo, resp);
                return resp;
            }
        }

        List<GiveUserDto> userList = batchReq.getUserList();
        if(CollUtil.isEmpty(userList)){
            throw new AppException(ResultCode.NOT_EXIST, "发放列表数据不存在!");
        }
        // 构建批次信息
        GiveBatchInfo batchInf = buildGrantBatchInfo(batchReq, userList);
        List<GiveLog> logs = Lists.newArrayList();
        for (GiveUserDto dto : userList) {
            GiveLog node = buildGiveLog(batchReq, batchInf, dto);
            logs.add(node);
        }



        return resp;
    }

    private GiveLog buildGiveLog(BatchGiveReq batchReq, GiveBatchInfo batchInf, GiveUserDto dto) {
        GiveLog node = new GiveLog();
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
     * @param batchInfo
     * @param resp
     */
    private void retryGrant(GiveBatchInfo batchInfo, GiveResp resp) {


    }
}
