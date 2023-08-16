package com.platform.authcommon.common.aop.aspect;


import cn.hutool.core.util.StrUtil;
import com.platform.authcommon.common.Constant;
import com.platform.authcommon.common.aop.annotation.DistributedLock;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Redisson分布式锁注解解析器
 *
 * @author lyl
 * @date 2021/11/11 3:27 下午
 */
@Aspect
@Component
@Slf4j
public class DistributedLockAspect {

    @Pointcut("@annotation(com.platform.authcommon.common.aop.annotation.DistributedLock)")
    public void lockPoint() {
    }

    @Autowired
    private RedisUtils redisClient;

    private final ExpressionParser parser = new SpelExpressionParser();

    private final LocalVariableTableParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);
        String prefix = distributedLock.prefix();
        String key = distributedLock.key();
        String cacheValue = distributedLock.cacheValue();
        boolean cacheFlag = StrUtil.isNotBlank(cacheValue);
        String cacheKeyName = Constant.DIS_KEY;
        Object[] args = pjp.getArgs();
        String keyName = prefix + parse(key, method, args);
        log.info("Redis分布式锁的key为 : {}", cacheKeyName);
        RLock lock = getLock(keyName, distributedLock);
        log.info("[开始]执行RedisLock环绕通知,获取Redis分布式锁开始");
        if (lock.tryLock(distributedLock.waitTime(), distributedLock.expireTime(), distributedLock.unit())) {
            try {
                log.info("获取Redis分布式锁[成功]，加锁完成key为 : {}，开始执行业务逻辑...", keyName);
                if (cacheFlag) {
                    String value = parse(cacheValue, method, args);
                    log.info("Redis缓存key为：{},value:{}", cacheKeyName, value);
                    redisClient.valueSet(cacheKeyName, value, 60 * 30);
                }
                return pjp.proceed();
            } finally {
                try {
                    lock.unlock();
                } catch (Exception ex) {
                    log.warn("释放Redis分布式锁[异常]", ex);
                }
                try {
                    if (cacheFlag) {
                        redisClient.delete(cacheKeyName);
                    }
                } catch (Exception ex) {
                    log.warn("删除缓存Key[异常]", ex);
                }
                log.info("释放Redis分布式锁[成功]，解锁完成key为 : {}，结束业务逻辑...", keyName);
            }
        } else {
            log.warn("获取Redis分布式锁[失败]key为 : {}", keyName);
            throw new AppException("获取分布式锁失败");
        }
    }

    /**
     * @description 解析spring EL表达式
     */
    private String parse(String key, Method method, Object[] args) {
        if (StrUtil.isBlank(key)) {
            return key;
        }
        String[] params = discoverer.getParameterNames(method);
        if (params == null || params.length <= 0) {
            return "";
        }
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < params.length; i++) {
            context.setVariable(params[i], args[i]);
        }
        StringBuilder sb = new StringBuilder();
        String[] keys = new String[]{key};
        if (key.contains(Constant.REDIS_KEY_CONNECTION_LINE)) {
            keys = key.split(Constant.REDIS_SPLIT_KEY_CONNECTION_LINE);
        }
        for (String s : keys) {
            sb.append(Constant.REDIS_SPLIT_LINE)
                    .append(parser.parseExpression(s).getValue(context, String.class));
        }
        return sb.toString();
    }

    private RLock getLock(String key, DistributedLock distributedLock) {
        switch (distributedLock.lockType()) {
            case REENTRANT_LOCK:
                return redisClient.getLock(key);

            case FAIR_LOCK:
                return redisClient.getFairLock(key);

            case READ_LOCK:
                return redisClient.getReadWriteLock(key).readLock();

            case WRITE_LOCK:
                return redisClient.getReadWriteLock(key).writeLock();
            default:
                throw new RuntimeException("do not support lock type:" + distributedLock.lockType().name());
        }
    }


}
