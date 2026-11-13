package com.platform.contract;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
@MapperScan("com.platform.contract.mapper")
public class ContractBootApplication {

    public static void main(String[] args) {

        TimeInterval timer = DateUtil.timer();
        SpringApplication.run(ContractBootApplication.class, args);
        log.info("start platform app !, cost time {}", timer.interval());

    }

}
