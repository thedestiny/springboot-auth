package com.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;


@Configuration
public class AppStaticConfig extends WebMvcConfigurationSupport  {

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
       //  registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/static/");
        // super.addResourceHandlers(registry);
    }

}
