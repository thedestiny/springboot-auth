package com.platform.flex;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description
 * @Date 2023-11-14 11:01 AM
 */
@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
@SpringBootConfiguration
@EnableConfigurationProperties
@MapperScan(basePackages = "com.platform.flex.mapper")
public class FlexApplication {


    public static void main(String[] args) {

        log.info("start product flex ! ");
        SpringApplication.run(FlexApplication.class, args);


    }
}
