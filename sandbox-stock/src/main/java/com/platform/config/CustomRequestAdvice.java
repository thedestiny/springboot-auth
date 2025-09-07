package com.platform.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * 可以使用数据加密，日志及打印，数据格式转换，设置响应头等信息。
 *
 * DispatchServlet
 * HandlerMapping
 * HandlerAdapter
 * HttpMessageConverter
 *
 *
 * @Description
 * @Author liangkaiyang
 * @Date 2025-08-28 4:37 PM
 */

@Slf4j
@ControllerAdvice
public class CustomRequestAdvice implements RequestBodyAdvice {


    @Override
    public boolean supports(MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> klass) {
        // 这里可以根据需要判断是否支持特定的请求
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage message, MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> klass) throws IOException {
        // 这里可以对请求体进行修改
        // 在请求体被读取之前，可以对输入流进行处理
        // 例如：记录日志、解密等

        return null;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage message, MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> klass) {
        // 请求体被读取之后，可以对其进行处理
        // 例如：修改请求体内容
        return body;
    }

    @Override
    public Object handleEmptyBody(Object body, HttpInputMessage message, MethodParameter parameter, Type type, Class<? extends HttpMessageConverter<?>> klass) {
        return body;
    }
}
