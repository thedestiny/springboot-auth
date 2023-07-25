package com.platform.orderserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

/**
 * @Description appId-客户端公钥配置
 * @Author liangwenchao
 * @Date 2023-06-28 3:11 PM
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "app")
@PropertySource(value = "classpath:pub_pri_key.properties", encoding = "UTF-8")
public class AppKeyConfig {

    //--------------- 服务端配置客户端公钥 ---------------------
    private Map<String, String> keys;


}
