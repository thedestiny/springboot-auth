package com.platform;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;


@Slf4j
@EnableAsync
@EnableCaching
@EnableScheduling
@SpringBootApplication
@MapperScan(basePackages = "com.platform.mapper")
public class StockApplication {

    public static void main(String[] args) {
        log.info("start stock app ! ");
        SpringApplication.run(StockApplication.class, args);
    }


    // 本地缓存处理器
    @Bean
    @Primary
    @Cacheable
    public CacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        // 全局缓存配置
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)  // 写入后30分钟过期
                .maximumSize(1000)  // 最大缓存条目数
                .recordStats());  // 记录缓存统计信息
        // 预设缓存名称
        cacheManager.setCacheNames(Arrays.asList("users", "products", "orders"));
        return cacheManager;
    }

//    @Bean
//    public CacheManager redisCacheManager(RedisConnectonFactory connectionFactory) {
//        // Redis缓存配置
//        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .entryTtl(Duration.ofMinutes(60))  // 设置TTL为60分钟
//                .disableCachingNullValues()  // 禁止缓存null值
//                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
//
//        // 为不同的缓存设置不同的配置
//        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
//        cacheConfigurations.put("users", cacheConfiguration.entryTtl(Duration.ofMinutes(10)));
//        cacheConfigurations.put("products", cacheConfiguration.entryTtl(Duration.ofHours(1)));
//
//        return RedisCacheManager.builder(connectionFactory)
//                .cacheDefaults(cacheConfiguration)
//                .withInitialCacheConfigurations(cacheConfigurations)
//                .build();
//    }

}
