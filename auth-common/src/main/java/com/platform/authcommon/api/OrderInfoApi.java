package com.platform.authcommon.api;


import com.platform.authcommon.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "order-server-api",url = "http://127.0.0.1:8078",path = "/order",configuration = OrderFeignConfig.class)
public interface OrderInfoApi {

    @PostMapping(value = "/order/info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    OrderDto queryOrderInfo(String order);

}
