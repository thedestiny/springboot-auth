package com.platform.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-27 3:06 下午
 */

@Data
@Component
@ConfigurationProperties(prefix = "person")
public class Person {

    private String lastName;

    private Integer age;

    private Boolean boss;

    private Date birth;

    private Map<String, String> maps;

    private List<String> lists;

    private Map<String, List<String>> varmaplist;


}
