package com.platform.flex.web;

import com.platform.flex.dto.StudentDto;
import com.platform.flex.mq.MsgProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping(value = "order")
    public String order(@RequestBody StudentDto dto) {

        msgProvider.sendOrderMessage(dto);
        return "success";
    }


}
