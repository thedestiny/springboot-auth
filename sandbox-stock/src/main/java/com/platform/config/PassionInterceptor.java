package com.platform.config;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.platform.utils.RequestIdHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.MaskFormatter;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
// 项目中配置 app.enable.passion-interceptor 时项目才会注入 bean
@ConditionalOnProperty(value = {"app.enable.passion-interceptor"})
public class PassionInterceptor extends HandlerInterceptorAdapter implements Serializable {
    private static final long serialVersionUID = 8006753431020928330L;

    private static final String REQUESTID = "requestId";

    // 令牌桶限流
    private static final RateLimiter rateLimiter = RateLimiter.create(5.0); // 每秒5个令牌

    public static boolean tryAcquire() {
        return rateLimiter.tryAcquire(); // 尝试获取令牌，如果没有可用令牌则返回false
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!tryAcquire()){
            // todo 返回限流系统信息。·
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
            return false;
        }
        String url = request.getRequestURL().toString();
        String id = RequestIdHelper.generate();
        MDC.put(REQUESTID, id);
        log.info("-------------------------------- start ---------------------------------------");
        log.info("请求地址 -> {} {}", request.getMethod(), url);
        log.info("reqId -> {}", id);
        log.info("请求参数 {}", JSONObject.toJSON(trans(request.getParameterMap())));
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        MDC.remove(REQUESTID);
        // String url = request.getRequestURL().toString();
        // String id = RequestIdHelper.generate();
        // MDC.put(REQUESTID, id);
        // logger.info("-------------------------------- start ---------------------------------------");
        // logger.info("请求地址 -> {} {}", request.getMethod(), url);
        // logger.info("reqId -> {}", id);
        // logger.info("-------------------------------- end ---------------------------------------");

    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        super.afterConcurrentHandlingStarted(request, response, handler);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

    public Map<String, String> trans(Map<String, String[]> map) {

        Map<String, String> result = new HashMap<String, String>();
        if (CollUtil.isEmpty(map)) {
            return result;
        }

        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            String key = entry.getKey();
            String[] value = entry.getValue();
            result.put(key, format(value));
        }
        return result;
    }

    public String format(String[] arr) {
        if (arr == null) {
            return "";
        }
        if (arr != null && arr.length == 1) {
            return arr[0];
        }

        return Arrays.toString(arr);
    }


}
