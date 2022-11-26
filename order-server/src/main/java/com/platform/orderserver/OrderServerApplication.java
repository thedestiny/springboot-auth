package com.platform.orderserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class OrderServerApplication {

    public static void main(String[] args) {
        log.info("start order! ");
        SpringApplication.run(OrderServerApplication.class, args);
    }

}
