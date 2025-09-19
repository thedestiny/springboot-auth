package com.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-09-19 2:17 PM
 */

@Slf4j
@Configuration
public class AppMethodArgumentResolver implements HandlerMethodArgumentResolver  {


    private final String USER_INFO = "USER_INFO";
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 参数包含注解且参数类型为 UserInfoReq
        return parameter.getParameterType().isAssignableFrom(UserInfoReq.class)
                && parameter.hasMethodAnnotation(UserInfo.class);
    }



    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        return this.getTokenCheckResult(nativeWebRequest);
    }

    private UserInfoReq getTokenCheckResult(NativeWebRequest nativeWebRequest) {
        UserInfoReq userInfo = (UserInfoReq)nativeWebRequest.getAttribute(USER_INFO, 0);

        if (userInfo == null) {
            // 获取 HttpRequest 请求头中获取token 并查询用户信息
            HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
            // todo 获取 token 并查询用户信息
            userInfo = new UserInfoReq();
            // 从 request 中获取 token 校验结果
            if (userInfo == null) {
                userInfo = new UserInfoReq();
            }
            nativeWebRequest.setAttribute(USER_INFO, userInfo, 0);
        }

        return userInfo;
    }
}
