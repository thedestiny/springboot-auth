package com.platform.authgateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import com.platform.authcommon.common.Result;
import com.platform.authcommon.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 白名单路径访问时需要移除JWT请求头
 */
@Slf4j
@Component
public class AppGatewayFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 获取请求和响应信息
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        Result<Object> failed = Result.failed(ResultCode.UNAUTHORIZED, "登录失败");
        // 获取请求头中的 Authorization 信息
        String authorization = request.getHeaders().getFirst("Authorization");
        log.info("gateway filter uri is {} auth {}", exchange.getRequest().getURI().getPath(), authorization);
        if (StrUtil.isEmpty(authorization)) {
            DataBuffer buffer = response.bufferFactory().wrap(JSONObject.toJSONString(failed).getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        // 获取token 信息并将token 信息进行转换，获取 payload 信息
        authorization = authorization.replace("Bearer ", "");
        String payload = "";
        try {
            payload = StrUtil.toString(JWSObject.parse(authorization).getPayload());
        } catch (Exception e) {
            DataBuffer buffer = response.bufferFactory().wrap(failed.toString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }

        if (StrUtil.isEmpty(payload)) {
            DataBuffer buffer = response.bufferFactory().wrap(failed.toString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        //从token中解析用户信息并设置到Header中去
        log.info("AuthGlobalFilter.filter() user:{}", payload);
        ServerHttpRequest req = exchange.getRequest().mutate().header("user", payload).build();
        exchange = exchange.mutate().request(req).build();
        return chain.filter(exchange);


    }
}
