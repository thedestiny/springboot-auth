package com.platform.productserver.web;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.platform.authcommon.common.Result;
import com.platform.productserver.order.OrderReq;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单服务
 */
@Slf4j
@RestController
@RequestMapping(value = "api/order")
public class OrderController {


    @ApiOperation("订单创建")
    @PostMapping(value = "create")
    public Result<Boolean> createOrder(@RequestBody OrderReq orderReq){

        DateTime parse = DateUtil.parse("2024-01-01 03:00", "yyyy-MM-dd HH:mm");


        return Result.success(true);
    }















}
