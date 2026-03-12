package com.platform.sandboxsatoken.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 路由拦截器配置
 * 用于控制哪些路径需要登录验证，哪些路径放行
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> {
            // 登录校验：除白名单外，所有接口都需要登录
            SaRouter.match("/**")
                    .notMatch("/user/login", "/user/register", "/user/isLogin")
                    .notMatch("/test/public")
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}
