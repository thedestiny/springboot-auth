package com.platform.productserver;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;


@Slf4j
@RefreshScope
@EnableAsync
@EnableScheduling
@EnableDiscoveryClient
@SpringBootApplication
@SpringBootConfiguration
@EnableFeignClients(value = "com.platform.authcommon.api")
@EnableConfigurationProperties
@MapperScan(basePackages = "com.platform.productserver")
public class ProductServerApplication {

    public static void main(String[] args) {
        log.info("start product server! ");
        SpringApplication.run(ProductServerApplication.class, args);
    }

}
