package com.platform.orderserver.mapper;

import com.platform.orderserver.entity.Order;

import java.util.List;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-07-14 5:16 PM
 */
public interface OrderMapper {


    /**
     * SELECT DATE_ADD(NOW(), INTERVAL 30 MINUTE); 当前时间加上 30分钟
     * SELECT DATE_SUB(NOW(), INTERVAL 30 MINUTE); 当前时间减去 30分钟
     * 查询30分钟前处理中或者待支付的订单
     * @param order
     * @return
     */
    List<Order> selectOrderList(Order order);

}
