package com.platform.productserver.grant;

import cn.hutool.core.util.ObjectUtil;
import com.platform.authcommon.config.RedisUtils;
import com.platform.productserver.entity.GiveBatchInfo;
import com.platform.productserver.service.GiveBatchInfoService;
import com.platform.productserver.service.GiveLogService;
import com.platform.productserver.service.GiveRefundLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 积分分发
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
     * @param account
     * @return
     */
    public boolean point(GiveReq account) {






        return true;
    }

    public boolean pointBatch(BatchGiveReq batchReq) {
        // 查询红包批次信息
        String batchNo = batchReq.getBatchNo();
        GiveBatchInfo batchInfo = batchInfoService.queryBatchInfo(batchNo);
        // 分发批次信息已经存在，进行校验即可
        if(ObjectUtil.isNotEmpty(batchInfo)){

        } else {

        }


        return true;
    }
}
