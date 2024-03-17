package com.platform.config;

import cn.hutool.db.Db;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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


    public static void main(String[] args) {

        // 数据库连接配置
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/my_database");
        config.setUsername("my_user");
        config.setPassword("my_password");
        config.addDataSourceProperty("maximumPoolSize", 10);
        // hikari 数据库连接池
        HikariDataSource dataSource = new HikariDataSource(config);
        Db use = Db.use(dataSource, "com.mysql.cj.jdbc.Driver");
    }




}
