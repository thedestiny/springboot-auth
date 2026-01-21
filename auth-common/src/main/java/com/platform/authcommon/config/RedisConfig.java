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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.locks.Lock;

/**
 * @Description Redis 配置文件
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


    @Autowired
    private StringRedisTemplate redisTemplate;

    // 执行限流脚本
    private final static String rateLimit = "-- Lua脚本：user:limit:1001 是Key，ARGV[1]是当前时间戳（秒）\n" +
            "local now = tonumber(ARGV[1])\n" +
            "-- 清理1小时前的旧记录\n" +
            "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, now - 3600)\n" +
            "local count = redis.call('ZCARD', KEYS[1])\n" +
            "-- 未超5次则记录当前请求\n" +
            "if count < 5 then\n" +
            "    redis.call('ZADD', KEYS[1], now, now..math.random())\n" +
            "    redis.call('EXPIRE', KEYS[1], 3600) -- 自动过期，清理内存\n" +
            "    return 1 -- 放行\n" +
            "end\n" +
            "return 0 -- 限流";

    public void test(){
        DefaultRedisScript<Integer> redisScript = new DefaultRedisScript<>(rateLimit, Integer.class); // 使用 List 来接收返回值。
        String[] keys = new String[]{""};
        String[] args = new String[]{"value"};
        String[] result = stringRedisTemplate.execute(redisScript, keys, args);
        System.out.println("Result: " + Arrays.toString(result)); // 输出结果应该包括键和值
    }
        redisTemplate.
    }

    public static void main(String[] args) {


    }


}
