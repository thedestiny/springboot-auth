package com.platform.authcommon.config;

import cn.hutool.core.lang.UUID;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import javax.sql.DataSource;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * spring异步任务线程池 Async
 */

@Slf4j
@EnableAsync
@Configuration
public class AsyncPoolConfig implements AsyncConfigurer, SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(30);
        taskScheduler.setBeanName("schedule-task");
        taskScheduler.setThreadNamePrefix("task-");

        taskRegistrar.setScheduler(taskScheduler);
    }

    private static final int PROCESSORS = Runtime.getRuntime().availableProcessors();

    //  @EventListener
    // SimpleApplicationEventMulticaster  ApplicationEventMulticaster
    @Bean
    @Override
    public Executor getAsyncExecutor() {

        SimpleApplicationEventMulticaster multicaster = new SimpleApplicationEventMulticaster();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(PROCESSORS);
        executor.setMaxPoolSize(PROCESSORS * 2);
        executor.setQueueCapacity(1_000_000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("trade-async-");   // 这个非常重要

        // 等待所有任务结果候再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        // 定义拒绝策略 ThreadPoolExecutor
        executor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        // 初始化线程池, 初始化 core 线程
        executor.initialize();


        return executor;
    }


    /**
     * 捕捉IllegalArgumentException异常
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new MyAsyncExceptionHandler();
    }

    class MyAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
        @Override
        public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
            log.info("TASK Exception message - " + throwable.getMessage());
            log.info("Method name - " + method.getName());
            for (Object param : objects) {
                log.info("Parameter value - " + param);
            }
        }
    }


    /**
     * kafka 消费者配置
     */

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        // 1、基础连接配置（必选）
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "limit-power-execution-group");

        // 2、序列化配置（必选，根据业务调整类型）
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // 3、协作式模式核心配置（启用关键） 协作式粘性分配器
        props.put(
                ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG,
                Arrays.asList(
                        "org.apache.kafka.clients.consumer.CooperativeStickyAssignor"  // 重点：必须是Cooperative版本，而非普通Sticky
                )
        );
        // 4、强制配合配置（协作式模式必选，否则会触发异常Rebalance）
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);  // 禁用自动提交Offset
        // 补充：手动提交需注意的参数（避免消费超时触发Rebalance）
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);  // 5分钟，根据业务处理耗时调整
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);  // 单次拉取最大条数，避免处理耗时过长
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);  // 心跳间隔3s
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);  // 会话超时10s（建议是心跳间隔的3倍左右）
        // 5、可选优化配置（提升协作式模式稳定性）
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "cooperative-consumer-" + UUID.randomUUID().toString());  // 唯一客户端ID，便于监控定位
        props.put(ConsumerConfig.RECONNECT_BACKOFF_MS_CONFIG, 1000);  // 重连退避时间，避免频繁重试压垮Broker
        return new DefaultKafkaConsumerFactory<>(props);
    }


    public DataSource hikariConfig(){

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("url");
        config.setUsername("username");
        config.setPassword("password");
        config.setMaximumPoolSize(20);
        config.setMinimumIdle(3);


        return new HikariDataSource(config);

    }


}
