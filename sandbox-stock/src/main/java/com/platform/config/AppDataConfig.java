package com.platform.config;

import lombok.Data;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-27 3:16 下午
 */

@Data
@Configuration
@SpringBootConfiguration
public class AppDataConfig {

    @Value("${app.dataList}")
    private List<String> dataList;

    @Value("#{${app.hobbys}}")
    // @Value("${app.hobbys}")
    private Map<String,String> hobbys;


    @Value("#{${app.hobbyList}}")
    // @Value("${app.hobbys}")
    private Map<String, List<String>> hobbyList;


//    @Bean
//    public StringEncryptor stringEncryptor() {
//        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
//        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
//        // 配置加密密钥，务必保密
//        config.setPassword("mysecretkey");
//        // 设置加密算法
//        config.setAlgorithm("PBEWithMD5AndDES");
//        // 设置密钥迭代次数，影响破解难度
//        config.setKeyObtentionIterations("1000");
//        // 设置加密池的大小，1 表示单个实例使用
//        config.setPoolSize("1");
//        // 盐生成器类，防止彩虹表攻击
//        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
//        // 设置输出编码类型为 base64
//        config.setStringOutputType("base64");
//        encryptor.setConfig(config);
//        return encryptor;
//    }


}
