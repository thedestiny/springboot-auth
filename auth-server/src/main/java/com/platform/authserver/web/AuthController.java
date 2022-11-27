package com.platform.authserver.web;

import com.alibaba.fastjson2.JSONObject;
import com.platform.authserver.domain.Oauth2TokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

/**
 * 自定义Oauth2获取令牌接口
 * Created by macro on 2020/7/17.
 */
@Slf4j
@RestController
@RequestMapping("/oauth")
public class AuthController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * Oauth2登录认证
     * /oauth/token
     */
    @PostMapping(value = "/token")
    public OAuth2AccessToken accessToken(Principal principal,
                                                        @RequestParam Map<String, String> parameters)
            throws HttpRequestMethodNotSupportedException {

        OAuth2AccessToken token = tokenEndpoint.postAccessToken(principal, parameters).getBody();
        Oauth2TokenDto oauth2TokenDto = Oauth2TokenDto.builder()
                .token(token.getValue())
                .refreshToken(token.getRefreshToken().getValue())
                .expiresIn(token.getExpiresIn())
                .tokenHead("Bearer ").build();
        log.info("access token {}", JSONObject.toJSONString(token));
        return token;
    }
}
