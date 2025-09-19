package com.platform.config;

import java.lang.annotation.*;

/**
 * 自定义注解，用于获取当前登录用户的信息
 * @Description
 * @Author liangkaiyang
 * @Date 2025-09-19 2:21 PM
 */

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UserInfo {
}
