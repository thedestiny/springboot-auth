package com.platform.authgateway.authorization;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.dto.AuthConstant;
import com.platform.authcommon.dto.RedisConstant;
import com.platform.authcommon.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 鉴权管理器，用于判断是否有资源的访问权限
 */
@Slf4j
@Component
public class AuthorizationManager implements ReactiveAuthorizationManager<AuthorizationContext> {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Mono<AuthorizationDecision> check(Mono<Authentication> auth, AuthorizationContext context) {

        ServerHttpRequest request = context.getExchange().getRequest();
        String user = request.getHeaders().getFirst("user");
        // 获取用户权限信息
        UserDto userDto = JSONObject.parseObject(user, UserDto.class);
        if (userDto == null || CollUtil.isEmpty(userDto.getAuthorities())) {
            return Mono.just(new AuthorizationDecision(Boolean.FALSE));
        }
        // authorities 获取用户权限
        List<String> authorities = userDto.getAuthorities();
        // 管理员权限直接通过
        if (authorities.contains("ADMIN")) {
            return Mono.just(new AuthorizationDecision(Boolean.TRUE));
        }
        //从Redis中获取当前路径可访问角色列表
        URI uri = context.getExchange().getRequest().getURI();
        log.info(" check url {} and uri {}", request.getPath(), uri.getPath());
        Object obj = redisTemplate.opsForHash().get(RedisConstant.RESOURCE_ROLES_MAP, uri.getPath());
        List<String> requires = Convert.toList(String.class, obj);
        // 所在资源不需要授权,可以直接访问
        if(CollUtil.isEmpty(requires)){
            return Mono.just(new AuthorizationDecision(Boolean.TRUE));
        }
        // 查看资源是否和权限相适配
        List<String> tmpList = authorities.stream().map(ele -> ele = AuthConstant.AUTHORITY_PREFIX + ele).collect(Collectors.toList());
        for (String authority : tmpList) {
            if (requires.contains(authority)) {
                // 可以进行授权访问
                return Mono.just(new AuthorizationDecision(true));
            }
        }
        return Mono.just(new AuthorizationDecision(false));

//        //认证通过且角色匹配的用户可访问当前路径
//        return auth
//                .filter(Authentication::isAuthenticated)
//                .flatMapIterable(Authentication::getAuthorities)
//                .map(GrantedAuthority::getAuthority)
//                .any(node -> {
//                    log.info("node is {}", node);
//                    return tmpList.contains(node);
//                })
//                .map(AuthorizationDecision::new)
//                .defaultIfEmpty(new AuthorizationDecision(false));


    }

    @Override
    public Mono<Void> verify(Mono<Authentication> auth, AuthorizationContext object) {
        return null;
    }

}
