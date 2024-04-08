package com.platform.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * https://cloud.tencent.com/developer/article/2267078
 * protobuf
 * @Description
 * @Author kaiyang
 * @Date 2024-04-08 6:31 下午
 */


@Slf4j
@RequestMapping(value = "api/proto")
@RestController
public class ProtoController {


    @GetMapping(value = "proto")
    public String proto(){

        return "";
    }


}
