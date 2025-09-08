package com.platform.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * ResponseBodyAdvice 是 Spring MVC 提供的一个接口，
 * 用于在控制器方法返回的响应体被写入 HTTP 响应流之前对其进行统一处理。
 * 该接口允许开发者在全局范围内对响应数据进行修改、增强或封装，从而实现统一的响应格式、通用的响应逻辑等。
 * @Description
 * @Author liangkaiyang
 * @Date 2025-08-28 4:28 PM
 */


@Slf4j
@ControllerAdvice
public class CustomResponseAdvice implements ResponseBodyAdvice<Object> {


    /**
     * ResponseBodyAdvice 的主要作用是在响应体被序列化和写入 HTTP 响应之前进行干预
     */
    @Override
    public boolean supports(MethodParameter parameter, Class<? extends HttpMessageConverter<?>> klass) {
        // 排除特定的返回类型或注解
        return true;
    }



    @Override
    public Object beforeBodyWrite(Object body, MethodParameter parameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> klass, ServerHttpRequest request, ServerHttpResponse response) {


        Method method = parameter.getMethod();
        Class<?> returnClass = method.getReturnType();
        Boolean enable = true;// apiSecurityProperties.getEnable();
        // 获取注解
//        ApiSecurity apiSecurity = method.getAnnotation(ApiSecurity.class);
//        if (Objects.isNull(apiSecurity)) {
//            apiSecurity = method.getDeclaringClass().getAnnotation(ApiSecurity.class);
//        }
//        if (enable && Objects.nonNull(apiSecurity) && apiSecurity.encryptResponse() && Objects.nonNull(body)) {
//            // 只需要加密返回data数据内容
//            if (body instanceof ResponseVO) {
//                body = ((ResponseVO) body).getData();
//            }
//            JSONObject jsonObject = encryptResponse(body);
//            body = jsonObject;
//        } else {
//            if (body instanceof String || Objects.equals(returnClass, String.class)) {
//                String value = objectMapper.writeValueAsString(ResponseVO.success(body));
//                return value;
//            }
//            // 防止重复包裹的问题出现
//            if (body instanceof ResponseVO) {
//                return body;
//            }
//        }
//        return ResponseVO.success(body);

        // 对响应体进行统一处理
        return new ResponseWrapper<>("SUCCESS", "body");

    }


    // 自定义的统一响应结构

    @Data
    private static class ResponseWrapper<T> {
        private String status;
        private T data;

        public ResponseWrapper(String status, T data) {
            this.status = status;
            this.data = data;
        }
    }

}
