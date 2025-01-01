package com.platform.practice.config;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * 多数据源配置
 */
@Configuration
public class DynamicDataSourceConfig {

//
//    @Bean
//    @Primary // 标记这是默认数据源
//    @ConfigurationProperties("spring.datasource.dynamic.datasource.master")
//    public DataSource masterDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//
//    @Bean
//    @ConfigurationProperties("spring.datasource.dynamic.datasource.slave")
//    public DataSource slaveDataSource() {
//        return DataSourceBuilder.create().build();
//    }

//    @Bean
//    public DataSource dynamicDataSource() {
//        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
//        Map<String, DataSource> dataSourceMap = new HashMap<>();
//        dataSourceMap.put("master", masterDataSource());
//        dataSourceMap.put("slave", slaveDataSource());
//
//        // 设置默认数据源
//        routingDataSource.setPrimary("master");
//        // 严格模式
//        routingDataSource.setStrict(true);
//        for (Map.Entry<String, DataSource> entry : dataSourceMap.entrySet()) {
//            routingDataSource.addDataSource(entry.getKey(), entry.getValue());
//        }
//
//
//        return routingDataSource;
//    }

}
