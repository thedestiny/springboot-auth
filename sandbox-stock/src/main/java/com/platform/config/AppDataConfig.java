package com.platform.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-27 3:16 下午
 */

@Data
@Configuration
@SpringBootConfiguration
public class AppDataConfig {

    @Value("${app.dataList}")
    private List<String> dataList;

    @Value("#{${app.hobbys}}")
    // @Value("${app.hobbys}")
    private Map<String,String> hobbys;


    @Value("#{${app.hobbyList}}")
    // @Value("${app.hobbys}")
    private Map<String, List<String>> hobbyList;



}
