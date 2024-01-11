package com.platform.authcommon.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 *
 * @Description  Redis 配置文件
 */
@Slf4j
@Configuration
public class RedisConfig {

    @Autowired
    private RedisProperties properties;

    private static final String prefix = "redis://";


    @Bean(value = "redisClient")
    public RedissonClient initRedissonClient() {
        log.info("init Redisson config start");
        Config config = new Config();
        // 采用单机模式 useSingleServer
        // 采用集群模式 useClusterServers
        // redis://localhost:6379
        String address = prefix + properties.getHost() + ":" + properties.getPort();
        config.useSingleServer()
                .setTimeout(20000)
                .setPassword(null)
                // .setPassword(properties.getPassword())
                .setAddress(address);
                //可以用"redis://"来启用SSL连接
                // .addNodeAddress(prefix + properties.getHost() + ":" + properties.getPort());
        RedissonClient redisson = Redisson.create(config);
        log.info("init Redisson config end");
        return redisson;
    }


    @Bean
    public RedisTemplate<String, ?> getRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, ?> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setEnableTransactionSupport(true);
        template.setConnectionFactory(factory);
        return template;
    }


}
