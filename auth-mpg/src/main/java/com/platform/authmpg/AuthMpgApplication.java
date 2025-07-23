package com.platform.authmpg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
public class AuthMpgApplication {

    public static void main(String[] args) {
        log.info("start app !");
        SpringApplication.run(AuthMpgApplication.class, args);
    }

}
