package com.platform.productserver.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * 提供全局获取AppContext的方法
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware, Ordered {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ApplicationContextHolder.applicationContext = context;
    }

    public static ApplicationContext get() {
        return applicationContext;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
