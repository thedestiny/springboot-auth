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
 * 认证服务器
 */

@Slf4j
@Configuration
@EnableAuthorizationServer // 开启认证服务
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

    // 采用数据库存储客户端服务信息
    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(dataSource);
    }
    // 采用数据库存储自动授权信息
    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(dataSource);
    }
    // 授权码存储配置
    @Bean
    public AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
    // 配置令牌访问的端点安全约束
    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        log.info(" configure !");
        // 允许表单提交
        oauthServer.allowFormAuthenticationForClients()
                .checkTokenAccess("isAuthenticated()") // 需要授权  /oauth/check_token
                .tokenKeyAccess("permitAll()") //  公开 /oauth/token_key
                // 使用不加密的方式，或者使用 PasswordEncoder 的方式来处理 client_secret 加密问题
                // .passwordEncoder(passwordEncoder)
                .passwordEncoder(NoOpPasswordEncoder.getInstance());

    }

    // 用来配置令牌的访问端点和令牌服务
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> delegates = new ArrayList<>();
        delegates.add(tokenEnhancer);
        delegates.add(accessTokenConverter());
        enhancerChain.setTokenEnhancers(delegates); //配置JWT的内容增强器

        endpoints
                // 设置异常WebResponseExceptionTranslator，用于处理用户名，密码错误、授权类型不正确的异常
                // .exceptionTranslator(new OAuthServerWebResponseExceptionTranslator())
                .authenticationManager(authenticationManager) // 密码模式下需要
                .reuseRefreshTokens(false) // refresh_token 是否重复使用
                .userDetailsService(userDetailsService) //配置加载用户信息的服务
                // .authorizationCodeServices(authorizationCodeServices()) // 授权码模式需要
                // .tokenServices(tokenServices())
                .accessTokenConverter(accessTokenConverter())
                .allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST) //支持GET,POST请求
                .tokenEnhancer(enhancerChain)
        ;
        // 重写授权页面
        endpoints.pathMapping("/oauth/confirm_access","/custom/confirm_access");

    }
    // 配置客户端的详情服务信息，客户端的信息在这里进行初始化，可以采用 inMemory 的方式，但这里使用的是数据库 存储
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

        // 可以进行内存配置 此处使用数据库配置
        JdbcClientDetailsService service = clientDetailsService();
//        service.setPasswordEncoder(passwordEncoder);
        clients.withClientDetails(service);

//        clients.inMemory()
//                .withClient("clientapp")
//                .secret("112233") // Client 账号、密码。
//                .authorizedGrantTypes("authorization_code") // 密码模式
////              .authorizedGrantTypes("passowrd") // 密码模式
//                .redirectUris("http://127.0.0.1:9501/callback") // 配置回调地址，选填。
//                .scopes("read_userinfo", "read_contacts") // 可授权的 Scope
//                .accessTokenValiditySeconds(360000)
//                .refreshTokenValiditySeconds(360000)
//                .authorities("user","admin")
//                .resourceIds();

    }
    // jwt token 信息
    @Bean
    public JwtTokenStore jwtTokenStore() {
        //基于内存的普通令牌  InMemoryTokenStore  JdbcTokenStore  JwkTokenStore RedisTokenStore
        //return new InMemoryTokenStore();
        return new JwtTokenStore(accessTokenConverter());
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        // https://blog.csdn.net/m0_45406092/article/details/121065177
        String SIGN_KEY = "admin";
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        // 对称秘钥，资源服务器使用该秘钥来验证
        // converter.setSigningKey(SIGN_KEY);
        converter.setKeyPair(keyPair());
        return converter;
    }

    @Bean
    public KeyPair keyPair() {
        //从classpath下的证书中获取秘钥对
        KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "123456".toCharArray());
        return keyStoreKeyFactory.getKeyPair("jwt", "123456".toCharArray());
    }



//    @Override
//    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//        endpoints.authenticationManager(authenticationManager);
//    }

    /**
     * 令牌管理服务的配置
     */
//    @Bean
//    public AuthorizationServerTokenServices tokenServices() {
//        DefaultTokenServices services = new DefaultTokenServices();
//        //客户端端配置策略
//        services.setClientDetailsService(clientDetailsService());
//        //支持令牌的刷新
//        services.setSupportRefreshToken(true);
//        //令牌服务 jdbcTokenStore jwtTokenStore
//        services.setTokenStore(jwtTokenStore());
//        //access_token的过期时间
//        services.setAccessTokenValiditySeconds(60 * 60 * 24 * 3);
//        //refresh_token的过期时间
//        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
//        //设置令牌增强，使用JwtAccessTokenConverter进行转换
//        services.setTokenEnhancer(tokenEnhancer);
//        return services;
//    }


}
