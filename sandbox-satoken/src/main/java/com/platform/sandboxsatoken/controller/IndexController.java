package com.platform.sandboxsatoken.controller;


import cn.hutool.core.util.StrUtil;
import com.platform.sandboxsatoken.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class IndexController {


    @GetMapping("/index")
    public Result<String> index() {

        String world = StrUtil.format("hello world");
        log.info("world: {}", world);


        return Result.ok("hello world");
    }

}
