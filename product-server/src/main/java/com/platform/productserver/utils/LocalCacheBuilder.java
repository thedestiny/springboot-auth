package com.platform.productserver.utils;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Date 2023-08-16 12:58 PM
 */
@Slf4j
public class LocalCacheBuilder {

    /**
     * 初始化  Cache Builder
     * 初始化数据容量
     * 最大容量
     * 并发度
     * 写入后多久过期
     */
    public static final Cache<String, String> localCache = CacheBuilder.newBuilder()
            .initialCapacity(100)
            .maximumSize(10000)
            .concurrencyLevel(3)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();


}
