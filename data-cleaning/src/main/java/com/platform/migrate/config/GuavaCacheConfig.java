package com.platform.migrate.config;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-13 5:32 PM
 */

@Slf4j
public class GuavaCacheConfig {

    public static final Cache<String, String> cache = CacheBuilder.newBuilder()
            .initialCapacity(5)  // 初始容量
            .maximumSize(10)     // 最大缓存数，超出淘汰
            .expireAfterWrite(60, TimeUnit.SECONDS) // 过期时间
            .build();


    public static void main(String[] args) {


        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.put("key3", "value3");
        cache.put("key4", "value4");
        cache.put("key5", "value5");
        cache.put("key6", "value6");
        cache.put("key7", "value7");
        cache.put("key8", "value8");
        cache.put("key9", "value9");
        cache.put("key10", "value10");
        cache.put("key11", "value11");

    }

}
