package com.platform.productserver.service;


import com.platform.productserver.dto.FundDto;

import java.util.List;

/**
 * @Description
 * @Date 2023-08-16 10:21 AM
 */
public interface StockService{


    Integer saveFundInfoList(List<FundDto> fundDtos);
}
