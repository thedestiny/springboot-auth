package com.platform.authserver.config;

import com.platform.authserver.domain.SecurityUser;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * jwt 内容增强
 */
@Component
public class JwtTokenEnhancer implements TokenEnhancer {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken token, OAuth2Authentication auth) {
        SecurityUser securityUser = (SecurityUser) auth.getPrincipal();
        Map<String, Object> info = new HashMap<>();
        //把用户ID设置到JWT中
        info.put("id", securityUser.getId());
        info.put("username", securityUser.getUsername());
//        info.put("authorities", securityUser.getAuthorities());
        ((DefaultOAuth2AccessToken) token).setAdditionalInformation(info);
        // ((DefaultOAuth2AccessToken) token).setRefreshToken(info);
        return token;
    }

}
