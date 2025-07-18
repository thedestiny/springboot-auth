package com.platform.authmpg.config;

import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * @Description 序列化的配置
 * @Author liangkaiyang
 * @Date 2025-07-18 3:44 PM
 */

@Configuration
public class HttpMessageConfig {


    @Bean
    public HttpMessageConverters fasJsonHttpMessageConverter() {
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = getJsonCofig();
        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
//        fastJsonHttpMessageConverter通过封装FastjsonConfig配置全局
        return new HttpMessageConverters(fastJsonHttpMessageConverter);
    }

    private FastJsonConfig getJsonCofig() {
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        // 序列化特性
         fastJsonConfig.setSerializerFeatures();
        //序列化配置-个性化
        // fastJsonConfig.setSerializeConfig();
//        fastJsonConfig.setParserConfig(); //反序列化配置
//        fastJsonConfig.setSerializeFilters(); //序列化过滤器
        return fastJsonConfig;
    }

}
