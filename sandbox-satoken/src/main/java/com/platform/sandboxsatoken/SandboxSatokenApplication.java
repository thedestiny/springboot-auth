package com.platform.sandboxsatoken;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@EnableAsync
@EnableScheduling
@EnableCaching
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.platform.sandboxsatoken.mapper")
public class SandboxSatokenApplication {

    public static void main(String[] args) {

        SpringApplication.run(SandboxSatokenApplication.class, args);
        log.info("start sa token app !");
    }

}
