package com.platform.authgateway.filter;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import com.platform.authgateway.config.RouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// nacos 配置信息监听器设置
@Slf4j
public class NacosConfigListener extends AbstractListener {

    private RouteService routeService;

    public NacosConfigListener(RouteService routeService) {
        this.routeService = routeService;
    }
    // 使用一个线程池来处理配置信息
    @Override
    public Executor getExecutor() {
        return Executors.newSingleThreadExecutor();
    }
    // 接受 nacos 的配置变动信息
    @Override
    public void receiveConfigInfo(String configInfo) {
        // 接收网络配置信息 并进行更新路由配置
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

}
