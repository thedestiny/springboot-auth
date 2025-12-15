package com.platform.migrate.config;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-13 5:43 PM
 */
public class EhcacheConfig {

    private static final String ORDER_CACHE = "orderCache";

    // CacheConfigurationBuilder
    // ResourcePoolsBuilder

    private static final CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
            // 创建cache实例
            .withCache(ORDER_CACHE, CacheConfigurationBuilder
                    // 声明一个容量为20的堆内缓存
                    .newCacheConfigurationBuilder(String.class, String.class, ResourcePoolsBuilder.heap(20)))
            .build(true);


    public static void main(String[] args) {

        // 获取cache实例
        Cache<String, String> cache = cacheManager.getCache(ORDER_CACHE, String.class, String.class);


    }
}
