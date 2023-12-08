package com.platform.messsage.web;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-12-08 11:22 AM
 */

@Slf4j
@RestController
public class IndexController {


    @Autowired
    private AmqpTemplate template;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    // https://blog.csdn.net/weixin_42039228/article/details/123493937
    @GetMapping(value = "index")
    public String index() {

        String format = DateUtil.format(new DateTime(), DatePattern.NORM_DATETIME_FORMAT);
        JSONObject json = new JSONObject();
        json.put("code", "001");
        json.put("message", "成功");
        json.put("date", format);
        // direct
        rabbitTemplate.convertAndSend("direct-exchange", "direct-route-key", json.toJSONString());
        // topic
        rabbitTemplate.convertAndSend("topic-exchange", "route-key", json.toJSONString());
        // fanout
        rabbitTemplate.convertAndSend("fanout-exchange", null, json.toJSONString());

        return "success";
    }


}
