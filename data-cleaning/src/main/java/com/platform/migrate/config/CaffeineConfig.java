package com.platform.migrate.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-13 5:34 PM
 */

@Slf4j
public class CaffeineConfig {



    public static final Cache<String, String> cache = Caffeine.newBuilder()
                .initialCapacity(5)
                // 超出时淘汰
                .maximumSize(10)
                //设置写缓存后n秒钟过期
                .expireAfterWrite(60, TimeUnit.SECONDS)
                //设置读写缓存后n秒钟过期,实际很少用到,类似于expireAfterWrite
                .expireAfterAccess(17, TimeUnit.SECONDS)
                .build();


    public static void main(String[] args) {


        String ele = cache.get("key1", k -> "value1");



    }


}
