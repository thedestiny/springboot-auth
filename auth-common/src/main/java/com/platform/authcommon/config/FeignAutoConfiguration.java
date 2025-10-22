package com.platform.authcommon.config;

import feign.Client;
import feign.Contract;
import feign.codec.Decoder;
import feign.codec.Encoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.cloud.openfeign.support.SpringDecoder;

/**
 * feign 自动配置
 * @Description
 * @Author liangkaiyang
 * @Date 2025-10-22 4:54 PM
 */


@Slf4j
@Configuration(proxyBeanMethods = false)
//@EnableConfigurationProperties({LocalFeignProperties.class})
//@Import({LocalFeignClientRegistrar.class})
@ConditionalOnProperty(value = "feign.local.enable", havingValue = "true")
public class FeignAutoConfiguration {


    static {
        log.info("feign local route started");
    }

    @Bean
    @Primary
    public Contract contract() {
        return new SpringMvcContract();
    }

    @Bean(name = "defaultClient")
    public Client defaultClient() {
        return new Client.Default(null, null);
    }

//    @Bean(name = "ribbonClient")
//    public Client ribbonClient(CachingSpringLoadBalancerFactory cachingFactory,
//                               SpringClientFactory clientFactory) {
//        return new LoadBalancerFeignClient(defaultClient(), cachingFactory,
//                clientFactory);
//    }

    /**
     * decoder
     * @return
     */
    @Bean
    public Decoder decoder() {
        HttpMessageConverter httpMessageConverter = new GsonHttpMessageConverter();
        ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters(httpMessageConverter);
        SpringDecoder springDecoder = new SpringDecoder(messageConverters);
        return new ResponseEntityDecoder(springDecoder);
    }

    /**
     * encoder
     * @return
     */
    @Bean
    public Encoder encoder() {
        HttpMessageConverter httpMessageConverter = new GsonHttpMessageConverter();
        ObjectFactory<HttpMessageConverters> messageConverters = () -> new HttpMessageConverters(httpMessageConverter);
        return new SpringEncoder(messageConverters);
    }



}
