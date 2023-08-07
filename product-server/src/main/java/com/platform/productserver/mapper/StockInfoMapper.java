package com.platform.productserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.productserver.entity.StockInfo;

/**
 * @Description
 * @Date 2023-08-07 9:44 AM
 */
public interface StockInfoMapper extends BaseMapper<StockInfo> {


    Integer saveStockInfo(StockInfo info);

}
