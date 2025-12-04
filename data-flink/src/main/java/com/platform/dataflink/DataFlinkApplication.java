package com.platform.dataflink;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class DataFlinkApplication {

    public static void main(String[] args) {
        log.info("start app flink! ");
        SpringApplication.run(DataFlinkApplication.class, args);
    }

}
