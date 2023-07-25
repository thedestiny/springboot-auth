package com.platform.productserver.config;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

/**
 * 微服务过滤器，解密网关传递的用户信息，
 * 将其放入request中，便于后期业务方法直接获取用户信息
 */
@Slf4j
@Component
public class AuthenticationFilter extends OncePerRequestFilter {
    /**
     * 具体方法主要分为两步
     * 1. 解密网关传递的信息
     * 2. 将解密之后的信息封装放入到request中
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //获取请求头中的加密的用户信息
        String token = request.getHeader("TOKEN_NAME");
        String queryStr = request.getQueryString();
        log.info(" query str {}", queryStr);
        log.info(" url {}", request.getRequestURL());
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            log.info(" header is {} and value {}", header, request.getHeader(header));

        }


        Map<String, String[]> parameterMap = request.getParameterMap();
        parameterMap.forEach((k, v)-> {
            log.info("params key {} and value {}", k, v );
        });


        log.info("filter token {}", token);
        if (StrUtil.isNotBlank(token)){
            //解密
//            String json = Base64.decodeStr(token);
//            JSONObject jsonObject = JSON.parseObject(json);
//            //获取用户身份信息、权限信息
//            String principal = jsonObject.getString(TokenConstant.PRINCIPAL_NAME);
//            String userId=jsonObject.getString(TokenConstant.USER_ID);
//            String jti = jsonObject.getString(TokenConstant.JTI);
//            Long expireIn = jsonObject.getLong(TokenConstant.EXPR);
//            JSONArray tempJsonArray = jsonObject.getJSONArray(TokenConstant.AUTHORITIES_NAME);
//            //权限
//            String[] authorities =  tempJsonArray.toArray(new String[0]);
//            //放入LoginVal
//            LoginVal loginVal = new LoginVal();
//            loginVal.setUserId(userId);
//            loginVal.setUsername(principal);
//            loginVal.setAuthorities(authorities);
//            loginVal.setJti(jti);
//            loginVal.setExpireIn(expireIn);
//            //放入request的attribute中
//            request.setAttribute(RequestConstant.LOGIN_VAL_ATTRIBUTE,loginVal);
        }
        chain.doFilter(request,response);
    }
}