package com.platform.practice;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@Slf4j
@SpringBootApplication
public class SandboxPracticeApplication {

    public static void main(String[] args) {

        TimeInterval timer = DateUtil.timer();
        SpringApplication.run(SandboxPracticeApplication.class, args);
        log.info("start practice cost time {} ms", timer.intervalMs());

    }

}
