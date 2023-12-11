package com.platform.messsage.web;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.messsage.common.AppConstant;
import com.platform.messsage.dto.MsgDto;
import com.platform.messsage.listener.AppSendMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-12-08 11:22 AM
 */

@Slf4j
@RestController
@RequestMapping(value = "api")
public class IndexController {


    @Autowired
    private AmqpTemplate template;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private AppSendMsg sendMsg;

    /**
     * localhost:8080/api/home
     * 消息发送
     */
    @GetMapping(value = "home")
    public String sendMsg() {

        String format = DateUtil.format(new DateTime(), DatePattern.NORM_DATETIME_FORMAT);
        MsgDto msg = new MsgDto();
        msg.setCode("1000");
        msg.setMessage("消息发送");
        msg.setDate(format);

        sendMsg.sendMsg("", "app.queue", JSONObject.toJSONString(msg));

        return "success";
    }


    // https://blog.csdn.net/weixin_42039228/article/details/123493937

    /**
     * localhost:8080/api/index?type=topic,direct,fanout
     * 消息发送
     */
    @GetMapping(value = "index")
    public String index(String type) {

        String format = DateUtil.format(new DateTime(), DatePattern.NORM_DATETIME_FORMAT);
        MsgDto msg = new MsgDto();
        msg.setCode("1000");
        msg.setMessage("消息发送");
        msg.setDate(format);

        if (StrUtil.equals("direct", type)) {
            // direct
            sendMessage(AppConstant.DIRECT_EXCHANGE, AppConstant.DIRECT_ROUTE_KEY, msg, "direct 消息");
        }
        if (StrUtil.equals("topic", type)) {
            // topic
            sendMessage(AppConstant.TOPIC_EXCHANGE, AppConstant.TOPIC_QUEUE_PHONE, msg, "topic 消息 phone");
            sendMessage(AppConstant.TOPIC_EXCHANGE, AppConstant.TOPIC_QUEUE_MALL, msg, "topic 消息 mall");
        }
        if (StrUtil.equals("fanout", type)) {
            // fanout
            sendMessage(AppConstant.FANOUT_EXCHANGE, null, msg, "fanout 消息");
        }
        rabbitTemplate.convertAndSend("app.queue", JSONObject.toJSONString(msg));
        return "success";
    }

    // 消息发送
    public void sendMessage(String exchange, String key, MsgDto data, String message) {
        data.setData(message);
        log.info("send message {} ", JSONObject.toJSONString(data));
        rabbitTemplate.convertAndSend(exchange, key, data);
    }


}
