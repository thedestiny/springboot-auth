package com.platform.authcommon.api;


import com.platform.authcommon.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "order-server")
public interface OrderApi {

    @PostMapping(value = "/order/info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    OrderDto queryOrderInfo(String order);

}
