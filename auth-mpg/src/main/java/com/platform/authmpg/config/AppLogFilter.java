package com.platform.authmpg.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * 单次执行保障：OncePerRequestFilter 基类确保每个请求只被过滤一次，避免重复处理。
 * 通过 shouldNotFilter 方法内部逻辑，自动识别同一请求的多次调度，确保 doFilterInternal 仅执行一次。
 * jwt 解析 日志记录
 * AopContext.currentProxy()).innerMethod();
 * @Description 应用日志过滤器
 * @Author liangkaiyang
 * @Date 2025-07-23 11:09 AM
 */

@Slf4j
public class AppLogFilter extends OncePerRequestFilter {

    // 请求包装器 ContentCachingRequestWrapper 允许缓存请求体，用于后续重复读取
    // 响应包装器 ContentCachingResponseWrapper 缓存响应输出流，支持在响应提交前修改内容（如添加签名、动态拼接数据）。

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 包装请求，缓存输入流
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        byte[] requestBody = wrappedRequest.getContentAsByteArray();
        // 包装响应，缓存输出流
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        // 记录请求日志（可在此处添加自定义逻辑）
        log.debug("Received request body: {}", new String(requestBody));
        // 传递包装后的请求，确保后续组件能重复读取
        filterChain.doFilter(wrappedRequest, wrappedResponse);

        // 响应后处理：添加签名
        byte[] responseBody = wrappedResponse.getContentAsByteArray();
        String signature = generateSignature(responseBody);
        wrappedResponse.setHeader("X-Response-Signature", signature);
        // 必须调用此方法将缓存内容写入原始响应
        wrappedResponse.copyBodyToResponse();

    }

    private String generateSignature(byte[] body) {
        // 自定义签名逻辑
        return Base64.getEncoder().encodeToString(body);
    }
}
