package com.platform.authgateway.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

// 配置 网关的配置信息，动态网关配置信息
@ConfigurationProperties(prefix = "gateway.routes.config")
@Component
@Data
public class GatewayRouteConfig {

    private String dataId;

    private String group;

    private String namespace;

}
