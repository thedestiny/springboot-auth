package com.platform.practice.web;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.alibaba.fastjson.JSONObject;
import com.platform.practice.dto.Result;
import com.platform.practice.utils.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.Enumeration;
import java.util.Map;

/**
 * 服务转发
 https://www.jb51.net/article/222259.htm
 */

@Slf4j
@RestController
public class RewardController {


    @Value("#{${app.service.uri}}")
    private Map<String, String> serviceMap;

    @Value("${app.service.server}")
    private String server;


    @RequestMapping(value = {"/**"})
    public void forward(HttpServletRequest request, HttpServletResponse response) {

        String uri = request.getRequestURI();
        // 与apollo配置对比，是否有匹配的uri
        String realuri = uri.replaceAll("/{2,}", "/");
        String tagServer = serviceMap.get(realuri);

        // 如果不存在，尝试获取真实的服务地址
        if (StrUtil.isBlank(tagServer)) {
            AntPathMatcher antPathMatcher = new AntPathMatcher();
            for (Map.Entry<String, String> entry : serviceMap.entrySet()) {
                boolean match = antPathMatcher.match(entry.getKey(), realuri);
                if (match) {
                    tagServer = entry.getValue();
                    break;
                }
            }
        }
        if (StrUtil.isBlank(tagServer)) {
            Result<String> success = Result.success("not match uri " + uri);
            ResponseHelper.response(response, JSONObject.toJSONString(success));
            return;
        }
        StringBuffer requestURL = request.getRequestURL();
        URL url = URLUtil.url(requestURL.toString());
        // 替换为目标 服务
        String tagUrl = StrUtil.replace(url.toString(), url.getAuthority(), server + tagServer);
        String method = request.getMethod().toUpperCase();
        log.info("forward url {} -> target server {}", requestURL.toString(), server + tagServer);
        log.info("target {}", tagUrl);
        Method method1 = matchMethod(method);
        if(method1 == null){
            log.info("没有匹配到请求方法");
            Result<String> success = Result.success("not match uri " + uri);
            ResponseHelper.response(response, JSONObject.toJSONString(success));
            return;
        }
        // AbstractHandlerMethodMapping
        // AbstractHandlerMethodMapping.lookupHandlerMethod

        // 根据请求方法和请求地址 创建请求对象
        HttpRequest httpRequest = HttpUtil.createRequest(method1, tagUrl);
        // 填充 header 信息
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            httpRequest.header(headerName, headerValue);
        }
        httpRequest.header("app-from-stock", "1");
        httpRequest.body(ResponseHelper.readData(request));
        httpRequest.timeout(60 * 1000);
        HttpResponse execute = httpRequest.execute();
        ResponseHelper.response(response, execute);

    }

    private Method matchMethod(String method) {
        for (Method ele : Method.values()) {
            if(StrUtil.equals(ele.name(), method)){
                return ele;
            }
        }
        return null;
    }



}
