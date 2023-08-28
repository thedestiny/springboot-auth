package com.platform.productserver.service;


import com.platform.productserver.dto.FundDto;
import com.platform.productserver.entity.EtfInfo;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-16 10:21 AM
 */
public interface StockService{


    /**
     * 保存场外基金信息
     * @param funds
     * @return
     */
    Integer saveFundInfoList(List<FundDto> funds);

    /**
     * 保存 etf 信息
     */
    Integer saveEtfInfoList(List<EtfInfo> etfs);

    /**
     * 查询 ETF 列表信息
     * @return
     */
    List<EtfInfo> queryEtfInfoList();

    /**
     * 更新 etf 信息
     */
    Integer updateEtfInfo(EtfInfo etfInfo);
}
