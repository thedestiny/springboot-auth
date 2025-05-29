package com.platform.orderserver.service.impl;

import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.OrderInfoDto;
import com.platform.orderserver.dto.OrderInfoReq;
import com.platform.orderserver.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-05-28 6:26 PM
 */

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {


    @Override
    public Result<OrderInfoDto> createOrder(OrderInfoReq infoReq) {
        return null;
    }



}
