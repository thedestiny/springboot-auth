package com.platform.authmpg.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;

/**
 * @Description apollo 变动通知
 * @Author liangkaiyang
 * @Date 2025-06-12 5:26 PM
 */
@Slf4j
@Configuration
public class ApolloChangeAware implements ApplicationContextAware  {

    private ApplicationContext applicationContext;

    @Resource
    private RefreshScope refreshScope;

    @ApolloConfigChangeListener
    public void onChange(ConfigChangeEvent changeEvent) {

        Object obj = new Object();

        log.info("================ apollo 自动刷新值 开始 ===========================");
        for (String changeKey : changeEvent.changedKeys()) {
            ConfigChange configChange = changeEvent.getChange(changeKey);
            String oldValue = configChange.getOldValue();
            String newValue = configChange.getNewValue();
            log.info("changed key:【{}】,old value=【{}】, new value:【{}】", changeKey, oldValue, newValue);
        }
        refreshProperties(changeEvent);
        log.info("================ apollo 自动刷新值 结束 ===========================");
    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(changeEvent.changedKeys()));
        refreshScope.refreshAll();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
