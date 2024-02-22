package com.platform.desen.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-22 6:44 PM
 */

@Data
@Configuration
public class MaskConfig implements Serializable {

    @Value("${log.mask.enable:true}")
    private Boolean enable;

    @Value("${log.mask.path}")
    private String path;


}
