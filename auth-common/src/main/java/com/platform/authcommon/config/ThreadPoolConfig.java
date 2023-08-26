package com.platform.authcommon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步任务线程池配置
 */
@Configuration
public class ThreadPoolConfig {


    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    /**
     * 执行线程池
     */
    @Bean
    public ExecutorService executorService() {
        long keepAliveTime = 1L;
        int maxQueueNum = 2000000;// 队列最大长度为200w
        // core线程和max线程数量均为 processors
        return new ThreadPoolExecutor(PROCESSORS, PROCESSORS,
                keepAliveTime, TIME_UNIT,
                new LinkedBlockingQueue(maxQueueNum),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }


}
