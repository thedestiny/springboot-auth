package com.platform.authmpg.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.concurrent.Executor;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-07-23 11:06 AM
 */

@Configuration
public class AppConfig {

    // 设置日志级别
    // logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG
    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true); // 包含查询参数
        filter.setIncludePayload(true);   // 包含请求体
        filter.setMaxPayloadLength(1024);  // 限制请求体日志长度（避免大字段溢出）
        filter.setAfterMessagePrefix("[REQUEST DATA] ");
        return filter;
    }

    /**
     * 自定义线程池
     */
    @Bean("customExecutor")
    public ThreadPoolTaskExecutor customExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(20_000_000);
        executor.initialize();
        return executor;
    }


}
