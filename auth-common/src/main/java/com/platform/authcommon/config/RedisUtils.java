package com.platform.authcommon.config;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.StrUtil;
import com.platform.authcommon.common.Constant;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
  redis 配置文件
 */

@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<String, Object> cacheRedis;

    @Autowired
    private RedissonClient redissonClient;

    @Value("${app.prefix:app}")
    private String prefix = "app";

    private String assemblyKey(String key) {
        StrBuilder sb = new StrBuilder();
        sb.append("{")
                .append(prefix)
                .append("::");
        if (key.contains(Constant.REDIS_SPLIT_LINE)) {
            String[] split = key.split(Constant.REDIS_SPLIT_LINE);
            sb.append(split[0])
                    .append("}");
            if (split.length > 1) {
                for (int i = 1; i < split.length; i++) {
                    String s = split[i];
                    if (StrUtil.isNotBlank(s)) {
                        sb.append("_").append(s);
                    }
                }
            }
        } else {
            sb.append(key).append("}");
        }
        return sb.toString();
    }

    public RLock getLock(String key) {
        return redissonClient.getLock(assemblyKey(key));
    }

    public RLock lock(String key, long time, TimeUnit timeUnit) {
        RLock lock = getLock(assemblyKey(key));
        lock.lock(time, timeUnit);
        return lock;
    }

    /**
     * 自增
     */
    public long increment(String key){
        return cacheRedis.opsForValue().increment(key);
    }

    public Boolean hasKey(String key) {
        return cacheRedis.hasKey(key);
    }

    public Boolean hasAssemblyKey(String key) {
        return cacheRedis.hasKey(assemblyKey(key));
    }


    public void zSetAdd(String key, String value, double score) {
        cacheRedis.opsForZSet().add(assemblyKey(key), value, score);
    }

    public Set<Object> zSetRangeByScore(String key, double v1, double v2) {
        return cacheRedis.opsForZSet().rangeByScore(assemblyKey(key), v1, v2);
    }

    public void zSetRemoveByRange(String key, Long v1, Long v2) {
        cacheRedis.opsForZSet().removeRange(assemblyKey(key), v1, v2);
    }

    /**
     * 设置缓存的过期时间
     * @param key 缓存 key
     * @param expires 过期时间 s
     */
    public void expire(String key,  int expires){
        cacheRedis.expire(key, expires, TimeUnit.SECONDS);
    }

    public Object valueGet(String key) {
        return cacheRedis.opsForValue().get(assemblyKey(key));
    }

    public void valueSet(String key, String value, int expires) {

        if (expires <= 0) {
            cacheRedis.opsForValue().set(assemblyKey(key), value);
        } else {
            cacheRedis.opsForValue().set(assemblyKey(key), value, (long) expires, TimeUnit.SECONDS);
        }
    }

    public RLock getFairLock(String key) {
        return redissonClient.getFairLock(assemblyKey(key));
    }

    public RReadWriteLock getReadWriteLock(String key) {
        return redissonClient.getReadWriteLock(assemblyKey(key));
    }

    public List<Object> listRange(String key, Long start, Long end) {
        return cacheRedis.opsForList().range(assemblyKey(key), start, end);
    }

    public Long listSize(String key) {
        return cacheRedis.opsForList().size(assemblyKey(key));
    }

    public void listTrim(String key, Long start, Long end) {
        cacheRedis.opsForList().trim(assemblyKey(key), start, end);
    }

    public void listRightPush(String key, Object value) {
        cacheRedis.opsForList().rightPush(assemblyKey(key), value);
    }

    public void listRightPushAll(String key, Collection value) {
        cacheRedis.opsForList().rightPushAll(assemblyKey(key), value);
    }

    public void listRightPushAll(String key, Object ... value) {
        cacheRedis.opsForList().rightPushAll(assemblyKey(key), value);
    }

    public void listLeftPush(String key, Object value) {
        cacheRedis.opsForList().leftPush(assemblyKey(key), value);
    }

    public void listLeftPushAll(String key, Collection value) {
        cacheRedis.opsForList().leftPushAll(assemblyKey(key), value);

    }

    public Object listLeftPop(String key) {
        return cacheRedis.opsForList().leftPop(assemblyKey(key));
    }

    public void delete(String key){
        cacheRedis.delete(assemblyKey(key));
    }
}
