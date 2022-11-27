package com.platform.authgateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.nimbusds.jose.JWSObject;
import com.platform.authcommon.common.Result;
import com.platform.authcommon.common.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 全局过滤器
 */

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        Result<Object> failed = Result.failed(ResultCode.UNAUTHORIZED, "登录失败");
        String authorization = request.getHeaders().getFirst("Authorization");
        log.info("auth filter uri is {} auth {}", exchange.getRequest().getURI().getPath(), authorization);
        if (StrUtil.isEmpty(authorization)) {
            DataBuffer buffer = response.bufferFactory().wrap(JSONObject.toJSONString(failed).getBytes());
            return response.writeWith(Mono.just(buffer));
        }
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

    @Override
    public int getOrder() {
        return 0;
    }
}
