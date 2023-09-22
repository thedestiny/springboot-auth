package com.platform.productserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.productserver.entity.GiveBatchInfo;

/**
 * <p>
 * 积分分发信息表 服务类
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
public interface GiveBatchInfoService extends IService<GiveBatchInfo> {

    /**
     * 根据批次号查询批次信息
     * @param batchNo
     * @return
     */
    GiveBatchInfo queryBatchInfo(String batchNo);


    Integer saveBatchInfo(GiveBatchInfo batchInfo);

    Integer updateBatchInfo(GiveBatchInfo batchInfo);


    Integer updateEntityById(GiveBatchInfo batchInf);
}
