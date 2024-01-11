package com.platform;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
@MapperScan(basePackages = "com.platform.mapper")
public class StockApplication {

    public static void main(String[] args) {
        log.info("start stock app ! ");
        SpringApplication.run(StockApplication.class, args);
    }

}
