package com.platform.migrate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * marathon
 */

@Slf4j
@SpringBootApplication
public class DataCleaningApplication {

    public static void main(String[] args) {
        log.info("start server ");
        SpringApplication.run(DataCleaningApplication.class, args);
    }

}
