package com.platform.productserver.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppRequestInterceptor implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate template) {
        // feign 调用添加请求头
        template.header("app-server","product-server");


    }


}
