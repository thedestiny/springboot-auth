package com.platform.flex.web;

import com.platform.flex.dto.OrderDto;
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


    // localhost:8097/api/msg
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

    /**
     * 顺序消息
     * localhost:8097/api/order/msg
     */
    @PostMapping(value = "order/msg")
    public String orderMsg(@RequestBody OrderDto dto) {
        msgProvider.sendOrderMessage(dto);
        return "success";
    }

    /**
     * 事务消息
     */
    @PostMapping(value = "tx/msg")
    public String txMsg(@RequestBody OrderDto dto) {
        msgProvider.sendTxMessage(dto);
        return "success";
    }
}
