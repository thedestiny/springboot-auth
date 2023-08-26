package com.platform.productserver.mapper;

import com.platform.productserver.entity.FundInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 基金基本信息表 Mapper 接口
 */
public interface FundInfoMapper extends BaseMapper<FundInfo> {

    /**
     * 保存基金信息
     */
    Integer saveFundInfo(FundInfo fund);

}
