package com.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 线程池维护线程的最少数量
        // asyncServiceExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors() + 1);
        executor.setCorePoolSize(5);
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

}
