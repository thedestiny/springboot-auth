package com.platform.authmpg.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.*;
import org.springframework.web.servlet.resource.ResourceUrlEncodingFilter;

import java.util.concurrent.Executor;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-07-23 11:06 AM
 */

@Configuration
public class AppConfig {

    // 设置日志级别
    // logging.level.org.springframework.web.filter.CommonsRequestLoggingFilter=DEBUG

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true); // 包含查询参数
        filter.setIncludePayload(true);   // 包含请求体
        filter.setMaxPayloadLength(1024);  // 限制请求体日志长度（避免大字段溢出）
        filter.setAfterMessagePrefix("[req-data]");
        return filter;
    }

    /**
     * 自定义线程池
     */
    @Bean("customExecutor")
    public ThreadPoolTaskExecutor customExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        executor.setQueueCapacity(20_000_000);
        executor.initialize();
        return executor;
    }


    // 字符串编码过滤器
    @Bean
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");       // ① 指定编码
        filter.setForceEncoding(true);      // ② 强制覆盖已有编码
        FilterRegistrationBean<CharacterEncodingFilter> bean = new FilterRegistrationBean<>(filter);
        bean.addUrlPatterns("/*");        // ③ 拦截所有请求
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);// ④ 最先执行，防止其他过滤器捣蛋
        return bean;
    }


    /**
     * 跨域配置
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);        // ① 允许携带 Cookie
        cfg.addAllowedOrigin("https://spa.xxx.com"); // ② 白名单域名
        cfg.addAllowedHeader("*");          // ③ 任意请求头
        cfg.addAllowedMethod("*");          // ④ 任意方法
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/api/**", cfg);
        return new CorsFilter(src);
    }

    /**
     * 静态资源 etag 缓存
     */
    @Bean
    public FilterRegistrationBean<ShallowEtagHeaderFilter> etag() {
        FilterRegistrationBean<ShallowEtagHeaderFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ShallowEtagHeaderFilter());
        bean.addUrlPatterns("/static/*", "/api/report/*");
        return bean;
    }

    /**
     * 静态资源 forward 缓存
     */
    @Bean
    public FilterRegistrationBean<ForwardedHeaderFilter> forwarded() {
        FilterRegistrationBean<ForwardedHeaderFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new ForwardedHeaderFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);  // 越早越好
        return bean;
    }

    /**
     * 静态资源 url 编码
     */
    @Bean
    public ResourceUrlEncodingFilter resourceUrlEncodingFilter() {
        return new ResourceUrlEncodingFilter();
    }

}
