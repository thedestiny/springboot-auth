package com.platform.productserver.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @Description
 * @Date 2023-12-22 5:11 下午
 */
@Aspect
@Component
@Slf4j
public class AppBusinessAspect {

    @Pointcut("@annotation(com.platform.productserver.config.Business)")
    public void lockPoint() {}


    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        Business distributedLock = method.getAnnotation(Business.class);
        // todo business
        return pjp.proceed();

    }

}
