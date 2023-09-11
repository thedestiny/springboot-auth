package com.platform.productserver.config;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.utils.IdGenUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AppInterceptor extends HandlerInterceptorAdapter implements Serializable {
    private static final long serialVersionUID = 8006753431020928330L;

    private static final String REQUESTID = "reqId";

    private Logger logger = LoggerFactory.getLogger(AppInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURL().toString();
        MDC.put(REQUESTID, IdGenUtils.id());
        logger.info("-------------------------------- start ---------------------------------------");
        logger.info("请求地址 -> {} {}", request.getMethod(), url);
        logger.info("请求参数 {}", JSONObject.toJSON(trans(request.getParameterMap())));
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) throws Exception {
        super.postHandle(request, response, handler, modelAndView);
    }
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        MDC.remove(REQUESTID);
    }
    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request,
                                               HttpServletResponse response, Object handler) throws Exception {
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
