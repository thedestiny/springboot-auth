package com.platform.orderserver.service;

import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.OrderInfoDto;
import com.platform.orderserver.dto.OrderInfoReq;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-05-28 6:24 PM
 */
public interface OrderService {

    /**
     * 创建订单
     */
    Result<OrderInfoDto> createOrder(OrderInfoReq infoReq);


}
