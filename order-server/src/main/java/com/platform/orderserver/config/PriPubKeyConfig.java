package com.platform.orderserver.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-06-27 6:32 PM
 */
@Data
@Configuration
@PropertySource(value = "classpath:pub_pri_key.properties", encoding = "UTF-8")
public class PriPubKeyConfig implements Serializable {

    // 服务端
    @Value("${server.pubkey}")
    private String serverPubKey;

    @Value("${server.prikey}")
    private String serverPriKey;

    // 客户端
    @Value("${client.pubkey}")
    private String clientPubKey;

    @Value("${client.prikey}")
    private String clientPriKey;


}
