package com.platform.messsage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 * @Date 2023-12-08 11:04 AM
 */

@Slf4j
@SpringBootApplication
public class BootApplication {


    public static void main(String[] args) {

        log.info("start product boot message ! ");
        SpringApplication.run(BootApplication.class, args);


    }


}
