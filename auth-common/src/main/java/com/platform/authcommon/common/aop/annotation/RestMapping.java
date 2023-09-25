package com.platform.authcommon.common.aop.annotation;

import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * 自定义注解
 * @Date 2023-09-25 2:24 PM
 */

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Mapping
public @interface RestMapping {


    String value() default "";

    RequestMethod method() default RequestMethod.GET;

}
