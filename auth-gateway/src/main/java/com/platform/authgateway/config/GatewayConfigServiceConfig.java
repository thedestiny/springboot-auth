package com.platform.authgateway.config;


import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

// 配置 nacos
@Configuration
public class GatewayConfigServiceConfig {

    @Autowired
    private GatewayRouteConfig routeConfig;

    @Autowired
    private NacosConfigProperties properties;

    // 创建 nacos ConfigService， 用来处理 nacos 配置服务
    @Bean
    public ConfigService configService() throws NacosException {
        Properties prop = new Properties();
        prop.setProperty(PropertyKeyConst.SERVER_ADDR, properties.getServerAddr());
        prop.setProperty(PropertyKeyConst.NAMESPACE, routeConfig.getNamespace());
        return NacosFactory.createConfigService(prop);
    }




}
