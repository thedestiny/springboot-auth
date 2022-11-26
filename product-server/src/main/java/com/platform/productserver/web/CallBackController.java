package com.platform.productserver.web;


import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.platform.productserver.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class CallBackController {


    @Autowired
    private AuthConfig authConfig;

    @GetMapping(value = "callback")
    public String callback(String code){

        HttpRequest post = HttpUtil.createPost(authConfig.accessTokenUri);

        Map<String,Object> params = new HashMap<>();

        params.put("client_id", authConfig.clientId);
        params.put("client_secret", authConfig.clientSecret);
        params.put("grant_type", "authorization_code");
        params.put("code", code);
//        params.put("redirect_uri", "http://localhost:9501/callback");

        post.form(params);
        HttpResponse execute = post.execute();
        String body = execute.body();
        log.info(" body is {}", body);
        return body;
    }


}
