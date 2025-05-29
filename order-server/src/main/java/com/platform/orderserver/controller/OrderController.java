package com.platform.orderserver.controller;


import com.platform.authcommon.api.OrderApi;
import com.platform.authcommon.common.Result;
import com.platform.authcommon.dto.OrderDto;
import com.platform.authcommon.exception.AppException;
import com.platform.orderserver.dto.OrderInfoDto;
import com.platform.orderserver.flownode.AppFlowDto;
import com.platform.orderserver.flownode.FlowExecutorService;
import com.platform.orderserver.service.OrderService;
import com.platform.orderserver.utils.IdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.omg.CORBA.portable.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import com.platform.orderserver.dto.OrderInfoReq;

import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class OrderController implements OrderApi {

    @Autowired
    private FlowExecutorService flowService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OrderService orderService;


    // 雪花算法ID生成器


    // 订单创建接口幂等性实现


    /**
     * 生成请求id
     */
    @GetMapping(value = "generate/request/id")
    public Result<String> generateRequestId() {
        return Result.success(IdGenUtils.genOrderNo());
    }

    /**
     * 生成订单创建接口幂等性实现
     */
    @PostMapping(value = "/create")
    public Result<OrderInfoDto> createOrder(@RequestBody OrderInfoReq infoReq,
                                            @RequestHeader("X-Request-ID") String requestId) {
        // 当前时间的时间戳
        long ct = System.currentTimeMillis();
        // 设置超时时间
        if (!redisTemplate.opsForValue().setIfAbsent(requestId, ct, 5, TimeUnit.MINUTES)) {
            throw new AppException("订单创建重复请求");
        }
        return orderService.createOrder(infoReq);
    }


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

    /**
     * 流程信息
     */
    @GetMapping(value = "flow")
    public String flow() {

        AppFlowDto cxt = new AppFlowDto();
        flowService.handleApp(cxt);
        return "success";
    }
}
