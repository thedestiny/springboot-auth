package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.FundInfo;

/**
 * 基金基本信息表 Mapper 接口
 */
public interface FundInfoMapper extends BaseMapper<FundInfo> {

    /**
     * 保存基金信息
     */
    Integer saveFundInfo(FundInfo fund);

}
