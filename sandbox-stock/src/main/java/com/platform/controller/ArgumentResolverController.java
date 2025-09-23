package com.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.platform.config.Person;
import com.platform.config.UserInfo;
import com.platform.config.UserInfoReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-09-19 2:19 PM
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class ArgumentResolverController {


    /**
     * 测试参数解析器
     * @param person
     * @param userInfoReq
     * @return
     */
    @PostMapping("/resolver/test")
    public String parameterResolver(@RequestBody Person person, @UserInfo UserInfoReq userInfoReq) {
        log.info("test person:{}", JSONObject.toJSONString(person));
        log.info("test userInfoReq:{}", JSONObject.toJSONString(userInfoReq));

        return "";
    }


}
