package com.platform.utils.encdec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.client.RestTemplate;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-04 2:50 PM
 */

@Slf4j
public class ServerTest {


    public static void main(String[] args) {


        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> param = new HashMap<>();
        param.put("age", "345");
        param.put("address", "河南郑州市巩义市");

        HttpHeaders headers = new HttpHeaders();
        headers.set("appId", "100100");
        headers.set("Content-Type", "application/json");

        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(param, headers);
        String body = restTemplate.postForObject("http://localhost:8080/api/v1/test/data", httpEntity, String.class);
        log.info("响应信息 resp ：{}", body);


    }
}
