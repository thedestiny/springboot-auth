package com.platform.utils;

import cn.hutool.core.date.DateUtil;
import lombok.Data;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
@ConfigurationProperties(value = "app.sftp")
public class FtpConfig implements Serializable {

    private static final long serialVersionUID = 80865837754814947L;

    // ftp 地址
    private String hostname;
    // 端口
    private Integer port;
    // 用户名
    private String username;
    // 密码
    private String password;
    // 私钥
    private String privateKey;
    // 上传路径
    private String uploadPath = "/upload";


}
