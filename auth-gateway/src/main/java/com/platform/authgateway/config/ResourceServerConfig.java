package com.platform.authgateway.config;

import cn.hutool.core.util.ArrayUtil;
import com.platform.authcommon.dto.AuthConstant;
import com.platform.authgateway.authorization.AuthorizationManager;
import com.platform.authgateway.component.RestAuthenticationEntryPoint;
import com.platform.authgateway.component.RestfulAccessDeniedHandler;
import com.platform.authgateway.filter.AppGatewayFilter;
import com.platform.authgateway.filter.AuthGlobalFilter;
import com.platform.authgateway.filter.IgnoreUrlsRemoveJwtFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 资源服务器配置
 */

@Slf4j
@Configuration
@EnableWebFluxSecurity
public class ResourceServerConfig {

    @Autowired
    private AuthorizationManager manager;
    @Autowired
    private IgnoreUrlsConfig ignoreUrlsConfig;
    @Autowired
    private RestfulAccessDeniedHandler deniedHandler;
    @Autowired
    private RestAuthenticationEntryPoint entryPoint;
    @Autowired
    private IgnoreUrlsRemoveJwtFilter jwtFilter;
    @Autowired
    private AppGatewayFilter gatewayFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

        http.oauth2ResourceServer().jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter());

        //自定义处理JWT请求头过期或签名错误的结果
        // http.oauth2ResourceServer().authenticationEntryPoint(entryPoint);
        //对白名单路径，直接移除JWT请求头
        http.addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.addFilterBefore(gatewayFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        http.authorizeExchange()
                .pathMatchers(ArrayUtil.toArray(ignoreUrlsConfig.getUrls(),String.class)).permitAll()//白名单配置
                .anyExchange().access(manager) //鉴权管理器配置
                .and()
                .exceptionHandling()
                .accessDeniedHandler(deniedHandler)//处理未授权
                .authenticationEntryPoint(entryPoint)//处理未认证
                .and().csrf().disable();

        return http.build();
    }




    @Bean
    public Converter<Jwt, ? extends Mono<? extends AbstractAuthenticationToken>> jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix(AuthConstant.AUTHORITY_PREFIX);
        converter.setAuthoritiesClaimName(AuthConstant.AUTHORITY_CLAIM_NAME);

        JwtAuthenticationConverter jwtAuth = new JwtAuthenticationConverter();
        jwtAuth.setJwtGrantedAuthoritiesConverter(converter);
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuth);

    }

}
