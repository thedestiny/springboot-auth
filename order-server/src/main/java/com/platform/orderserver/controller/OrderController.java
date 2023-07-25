package com.platform.orderserver.controller;


import com.platform.authcommon.api.OrderApi;
import com.platform.authcommon.dto.OrderDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@RestController
public class OrderController  implements OrderApi {


    @Override
    public OrderDto queryOrderInfo(String order) {
        log.info("order info is {}", order);
        OrderDto dto = new OrderDto();
        dto.setOrderNo("123");
        dto.setCreateTime(new Date());
        dto.setAmount(BigDecimal.ONE);
        dto.setName("中文");
        return dto;
    }
}
