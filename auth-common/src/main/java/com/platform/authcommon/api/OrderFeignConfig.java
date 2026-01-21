package com.platform.authcommon.api;

import cn.hutool.core.lang.UUID;
import com.google.common.util.concurrent.RateLimiter;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * 调用方式，添加拦截器
 * @Author liangkaiyang
 * @Date 2025-10-16 6:19 PM
 */
@Slf4j
public class OrderFeignConfig implements RequestInterceptor {

    private final static  RateLimiter limiter = RateLimiter.create(100,100, TimeUnit.SECONDS);

    /**
     *  订单服务feign调用拦截器,实现方式一
     */
    @Override
    public void apply(RequestTemplate template) {

        String param = template.toString();
        log.info("app-server {} 入参:{}", template.url(), param.replace(param.split("\\{")[0], ""));
        // feign 调用添加请求头
        template.header("app-server","order-server");
        template.header("X-Gaia-Api-Key", "1f35373ccda84693815fa12757e1cfab");
        template.header("Content-Type", "application/json;charset=utf-8");
        // 获取许可
        limiter.acquire();

    }

    /**
     *  feign调用拦截器,全局方式
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            template.header("X-Gaia-Api-Key", "1f35373ccda84693815fa12757e1cfaa");
            template.header("Content-Type", "application/json");
        };
    }

    public static void main(String[] args) {

        System.out.println(UUID.fastUUID().toString().replace("-",""));

    }
}
