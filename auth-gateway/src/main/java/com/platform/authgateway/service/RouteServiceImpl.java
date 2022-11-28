package com.platform.authgateway.service;

import com.platform.authgateway.config.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class RouteServiceImpl implements RouteService, ApplicationEventPublisherAware {

    // 提供了对路由的增加删除等操作
    @Autowired
    private RouteDefinitionWriter writer;
    // 发布事件的
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Override
    public void update(RouteDefinition routeDefinition) {
        log.info("更新路由配置项：{}", routeDefinition);
        this.writer.delete(Mono.just(routeDefinition.getId()));
        writer.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    @Override
    public void add(RouteDefinition routeDefinition) {
        log.info("新增路由配置项：{}", routeDefinition);
        writer.save(Mono.just(routeDefinition)).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }


}
