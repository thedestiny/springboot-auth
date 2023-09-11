package com.platform.productserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.productserver.entity.GiveBatchInfo;

/**
 * <p>
 * 积分分发信息表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
public interface GiveBatchInfoMapper extends BaseMapper<GiveBatchInfo> {


    GiveBatchInfo selectBatchInfo(String batchNo);


}
