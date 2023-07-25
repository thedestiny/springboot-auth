package com.platform.productserver.web;


import com.platform.productserver.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.oauth2.client.OAuth2RestTemplate;
//import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
//import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
//import org.springframework.security.oauth2.common.OAuth2AccessToken;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HomeController {

    @Autowired
    private AuthConfig authConfig;

//    @GetMapping("/callback")
//    public OAuth2AccessToken login(@RequestParam("code") String code) {
//
//        // 创建 AuthorizationCodeResourceDetails 对象
//        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
//        resourceDetails.setAccessTokenUri(authConfig.accessTokenUri);
//        resourceDetails.setClientId(authConfig.clientId);
//        resourceDetails.setClientSecret(authConfig.clientSecret);
//        // resourceDetails.setTokenName("Authorization");
//        // 创建 OAuth2RestTemplate 对象
//
//        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(resourceDetails);
//
//        restTemplate.getOAuth2ClientContext().getAccessTokenRequest().setAuthorizationCode(code); // 设置 code
//        // 通过这个方式，设置 redirect_uri 参数
//        restTemplate.getOAuth2ClientContext().getAccessTokenRequest().setPreservedState("http://127.0.0.1:9501/callback");
//
//        restTemplate.setAccessTokenProvider(new AuthorizationCodeAccessTokenProvider());
//        // OAuth2ClientContext context = restTemplate.getOAuth2ClientContext();
//        OAuth2AccessToken accessToken = restTemplate.getAccessToken();
//        log.info("access token {}", accessToken);
////        accessToken.getValue()
//        // 获取访问令牌
//        return accessToken;
//    }

}
