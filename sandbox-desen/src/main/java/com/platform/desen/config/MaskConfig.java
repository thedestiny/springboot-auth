package com.platform.desen.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-22 6:44 PM
 */

@Data
@Configuration(value = "maskConfig")
public class MaskConfig implements Serializable {

    private static final long serialVersionUID = -746315412160267803L;


    @Value("${log.mask.enable:true}")
    private Boolean enable;

    @Value("${log.mask.path:com.platform}")
    private String path;

    // 关键字规则
    @Value("${log.mask.keyword:true}")
    private Boolean keyword = true;

    // 正则表达式
    @Value("${log.mask.regrex:true}")
    private Boolean regrex = true;

    @Value("#{${log.mask.keywordMap}}")
    private Map<String, List<String>> keywordMap;

    @Value("#{${log.mask.regrexMap}}")
    private Map<String, String> regrexMap;



}
