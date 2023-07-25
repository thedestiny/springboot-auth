package com.platform.productserver.config;


import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@SpringBootConfiguration
@ConfigurationProperties(prefix = "app")
public class ProductAppConfig {

    private String name;

    private Integer age;

    private String address;


}
