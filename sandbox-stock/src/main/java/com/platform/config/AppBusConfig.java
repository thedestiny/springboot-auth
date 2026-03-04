package com.platform.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.ErrorHandler;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-28 5:23 PM
 */

@Configuration
public class AppBusConfig {


    @Bean("mvcAsyncTaskExecutor")
    public AsyncTaskExecutor asyncTaskExecutor() {

        ConcurrentHashMap data = new ConcurrentHashMap();
        HashMap<String,String> map = new HashMap<>(12,0.75f);
        map.put("corePoolSize","5");
        map.put("queueCapacity","3");
        map.put("maxPoolSize","10");


        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        // asyncServiceExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        executor.setCorePoolSize(5);
        executor.setQueueCapacity(3);
        // 线程池维护线程的最大数量
        executor.setMaxPoolSize(10);
        // 线程池所使用的缓冲队列
        executor.setQueueCapacity(10);
        //  asyncServiceExecutor.prefersShortLivedTasks();
        executor.setThreadNamePrefix("app-task-thread-");
//   asyncServiceExecutor.setBeanName("TaskId" + taskId);
        // asyncServiceExecutor.setKeepAliveSeconds(20);
        //调用者执行
        //  asyncServiceExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        // 线程全部结束才关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 如果超过60s还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }

    // 异步事件使用 @Async 或者 EventMulticaster 配置线程池
    @Bean
    public SimpleApplicationEventMulticaster simpleApplicationEventMulticaster() {

        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();
        // 设置异步线程池
        multicaster.setTaskExecutor(asyncTaskExecutor());
        // 设置错误处理器
        // multicaster.setErrorHandler(new LoggingErrorHandler());

        return multicaster;
    }

}
