package com.platform.productserver.service.impl;

import com.platform.productserver.mapper.StockInfoMapper;
import com.platform.productserver.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Date 2023-08-16 10:21 AM
 */

@Slf4j
@Service
public class StockServiceImpl implements StockService {

    @Autowired
    private StockInfoMapper stockInfoMapper;




}
