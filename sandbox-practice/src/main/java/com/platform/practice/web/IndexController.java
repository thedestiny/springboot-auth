package com.platform.practice.web;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2024-12-23 11:10 AM
 */

@Slf4j
@RestController
public class IndexController {

    @GetMapping(value = "/merchant/list")
    public String test(HttpServletRequest request){
        log.info("test {}", request.getRequestURI());
        return "";
    }

}
