package com.platform.orderserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 开启事务 开启异步任务 开启定时任务
 */
@Slf4j
@EnableScheduling
@EnableAsync
@EnableDiscoveryClient
@SpringBootApplication
public class OrderServerApplication {

    public static void main(String[] args) {
        log.info("start order! ");
        SpringApplication.run(OrderServerApplication.class, args);
    }

}
