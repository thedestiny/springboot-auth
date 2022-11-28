package com.platform.authgateway.config;


import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.ConfigService;
import com.platform.authgateway.filter.NacosConfigListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

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
        log.info("start gateway route config...");
        try {
            // getConfigAndSignListener()方法 发起长轮询和对dataId数据变更注册监听的操作
            // getConfig 只是发送普通的HTTP请求
            String initConfigInfo = configService.getConfigAndSignListener(routeConfig.getDataId(),
                    routeConfig.getGroup(), properties.getTimeout(), new NacosConfigListener(routeService));
            log.info("获取网关当前动态路由配置:\r\n{}", initConfigInfo);
            // 第一次初始化动态网关
            if (StringUtils.isNotEmpty(initConfigInfo)) {
                List<RouteDefinition> definitions = JSONArray.parseArray(initConfigInfo, RouteDefinition.class);
                for (RouteDefinition definition : definitions) {
                    routeService.add(definition);
                }
            } else {
                log.warn("no routes config");
            }
            log.info("finish gateway config ...");
        } catch (Exception e) {
            log.error("dynamic gateway config error ", e);
        }

    }


}
