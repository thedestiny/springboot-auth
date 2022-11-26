package com.platform.productserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.security.oauth2")
public class AuthConfig {

     public String clientId;

     public String clientSecret;

     public String tokenInfoUri;

     public String accessTokenUri;







}
