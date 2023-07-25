package com.platform.authserver.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description
 * @Author kaiyang
 * @Date 2022-11-16 5:26 PM
 */

@Slf4j
@RequestMapping
@Controller
public class IndexController {

    @GetMapping(value = "login")
    public String login(){
        log.info("info login! ");
        return "login";
    }


}
