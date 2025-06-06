package com.platform.authmpg.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-06 3:42 PM
 */


@Slf4j
@RestController
public class IndexController {


    @GetMapping(value = "index")
    public String index(){
        return "success";
    }


}
