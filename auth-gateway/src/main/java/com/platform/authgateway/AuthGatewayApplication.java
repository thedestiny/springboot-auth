package com.platform.authgateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication
public class AuthGatewayApplication {

    public static void main(String[] args) {

        log.info(" start app gateway ! ");
        SpringApplication.run(AuthGatewayApplication.class, args);
    }

}
