package com.platform.flex.web;

import com.platform.flex.mq.MsgProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 消息发送
 * @Date 2023-12-02 11:22 PM
 */

@Slf4j
@RestController
@RequestMapping(value = "/api")
public class MsgController {


    @Autowired
    private MsgProvider msgProvider;


    @GetMapping(value = "msg")
    public String msg() {

        msgProvider.sendMessage();
        return "success";
    }


}
