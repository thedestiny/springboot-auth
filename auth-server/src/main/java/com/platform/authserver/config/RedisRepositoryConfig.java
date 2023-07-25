package com.platform.authserver.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * redis 相关的配置
 */
@Slf4j
@Configuration
@EnableRedisRepositories
public class RedisRepositoryConfig {

    @Autowired
    private RedisConnectionFactory factory;

//    @Bean
//    public RedisTokenStore redisTokenStore() {
//        RedisTokenStore redisTokenStore = new RedisTokenStore(factory);
//        // 设置key的层级前缀
//        redisTokenStore.setPrefix("TOKEN:");
//        return redisTokenStore;
//    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {

        log.info(" start redis template !");
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);
        Jackson2JsonRedisSerializer<?> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;

    }



}
