package com.platform.authserver.config;

import com.platform.authserver.exception.OAuthServerWebResponseExceptionTranslator;
import org.springframework.core.io.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.*;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;


/**
 * ???????????????
 */

@Slf4j
@Configuration
@EnableAuthorizationServer // ??????????????????
public class OAuth2AuthorizationServer extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenEnhancer tokenEnhancer;
    @Autowired
    private DataSource dataSource;

    // ??????????????????????????????????????????
    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }
    // ???????????????????????????????????????
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }
    // ?????????????????????
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
    // ???????????????????????????????????????
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        log.info(" configure !");
        // ??????????????????
        oauthServer.allowFormAuthenticationForClients()
                .checkTokenAccess("isAuthenticated()") // ????????????  /oauth/check_token
                .tokenKeyAccess("permitAll()") //  ?????? /oauth/token_key
                // ??????????????????????????????????????? PasswordEncoder ?????????????????? client_secret ????????????
                // .passwordEncoder(passwordEncoder)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());

    }

    // ????????????????????????????????????????????????
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(tokenEnhancer);
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates); //??????JWT??????????????????

        endpoints
                // ????????????WebResponseExceptionTranslator????????????????????????????????????????????????????????????????????????
                // .exceptionTranslator(new OAuthServerWebResponseExceptionTranslator())
                .authenticationManager(authenticationManager) // ?????????????????????
                .reuseRefreshTokens(false) // refresh_token ??????????????????
                .userDetailsService(userDetailsService) //?????????????????????????????????
                // .authorizationCodeServices(authorizationCodeServices()) // ?????????????????????
                // .tokenServices(tokenServices())
                .accessTokenConverter(accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST) //??????GET,POST??????
                .tokenEnhancer(enhancerChain)
        ;
        // ??????????????????
        endpoints.pathMapping("/oauth/confirm_access","/custom/confirm_access");

    }
    // ???????????????????????????????????????????????????????????????????????????????????????????????? inMemory ?????????????????????????????????????????? ??????
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        // ???????????????????????? ???????????????????????????
        JdbcClientDetailsService service = clientDetailsService();
//        service.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(service);

//        clients.inMemory()
//                .withClient("clientapp")
//                .secret("112233") // Client ??????????????????
//                .authorizedGrantTypes("authorization_code") // ????????????
////              .authorizedGrantTypes("passowrd") // ????????????
//                .redirectUris("http://127.0.0.1:9501/callback") // ??????????????????????????????
//                .scopes("read_userinfo", "read_contacts") // ???????????? Scope
//                .accessTokenValiditySeconds(360000)
//                .refreshTokenValiditySeconds(360000)
//                .authorities("user","admin")
//                .resourceIds();

    }
    // jwt token ??????
    @Bean
    public JwtTokenStore jwtTokenStore() {
        //???????????????????????????  InMemoryTokenStore  JdbcTokenStore  JwkTokenStore RedisTokenStore
        //return new InMemoryTokenStore();
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        // https://blog.csdn.net/m0_45406092/article/details/121065177
        String SIGN_KEY = "admin";
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // ??????????????????????????????????????????????????????
        // converter.setSigningKey(SIGN_KEY);
        converter.setKeyPair(keyPair());
        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        //???classpath??????????????????????????????
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }



//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.authenticationManager(authenticationManager);
//    }

    /**
     * ???????????????????????????
     */
//    @Bean
//    public AuthorizationServerTokenServices tokenServices() {
//        DefaultTokenServices services = new DefaultTokenServices();
//        //????????????????????????
//        services.setClientDetailsService(clientDetailsService());
//        //?????????????????????
//        services.setSupportRefreshToken(true);
//        //???????????? jdbcTokenStore jwtTokenStore
//        services.setTokenStore(jwtTokenStore());
//        //access_token???????????????
//        services.setAccessTokenValiditySeconds(60 * 60 * 24 * 3);
//        //refresh_token???????????????
//        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
//        //???????????????????????????JwtAccessTokenConverter????????????
//        services.setTokenEnhancer(tokenEnhancer);
//        return services;
//    }


}
