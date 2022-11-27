package com.platform.authgateway.config;


import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RefreshScope
@Slf4j
@Component
public class GatewayRouteInitConfig {

    @Autowired
    private GatewayRouteConfig routeConfig;
    @Autowired
    private NacosConfigProperties properties;
    @Autowired
    private RouteService routeService;
    @Autowired
    private ConfigService configService;

    @PostConstruct
    public void init() {
        log.info("开始网关动态路由初始化...");
        try {
            // getConfigAndSignListener()方法 发起长轮询和对dataId数据变更注册监听的操作
            // getConfig 只是发送普通的HTTP请求
            String initConfigInfo = configService.getConfigAndSignListener(routeConfig.getDataId(),
                    routeConfig.getGroup(), properties.getTimeout(), new Listener() {
                        @Override
                        public Executor getExecutor() {
                            return Executors.newSingleThreadExecutor();
                        }

                        @Override
                        public void receiveConfigInfo(String configInfo) {
                            if (StringUtils.isNotEmpty(configInfo)) {
                                log.info("接收到网关路由更新配置：\r\n{}", configInfo);
                                List<RouteDefinition> definitions = JSONArray.parseArray(configInfo, RouteDefinition.class);
                                if (CollUtil.isNotEmpty(definitions)) {
                                    for (RouteDefinition definition : definitions) {
                                        log.info("config update route {}", definition);
                                        routeService.update(definition);
                                    }
                                }

                            } else {
                                log.warn("当前网关无动态路由相关配置");
                            }
                        }
                    });
            log.info("获取网关当前动态路由配置:\r\n{}", initConfigInfo);
            if (StringUtils.isNotEmpty(initConfigInfo)) {

                List<RouteDefinition> definitions = JSONArray.parseArray(initConfigInfo, RouteDefinition.class);
                if (CollUtil.isNotEmpty(definitions)) {
                    for (RouteDefinition definition : definitions) {
                        routeService.add(definition);
                    }
                }

            } else {
                log.warn("当前网关无动态路由相关配置");
            }
            log.info("结束网关动态路由初始化...");
        } catch (Exception e) {
            log.error("初始化网关路由时发生错误", e);
        }

    }


}
