package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.StockInfo;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-07 9:44 AM
 */
public interface StockInfoMapper extends BaseMapper<StockInfo> {


    Integer saveStockInfo(StockInfo info);

    /**
     * 查询股票信息列表
     * @param info
     * @return
     */
    List<StockInfo> selectStockList(StockInfo info);

}
