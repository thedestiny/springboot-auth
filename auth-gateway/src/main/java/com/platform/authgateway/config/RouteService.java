package com.platform.authgateway.config;

import org.springframework.cloud.gateway.route.RouteDefinition;

public interface RouteService {


    /**
     * 更新路由配置
     *
     * @param routeDefinition
     */
    void update(RouteDefinition routeDefinition);

    /**
     * 添加路由配置
     *
     * @param routeDefinition
     */
    void add(RouteDefinition routeDefinition);



}
