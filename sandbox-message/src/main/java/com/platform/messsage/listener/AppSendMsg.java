package com.platform.messsage.listener;

import com.platform.messsage.utils.OrderNoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Slf4j
@Component
public class AppSendMsg implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate template;

    @PostConstruct
    public void init() {
        // 设置回调函数，避免使用默认的 ConfirmCallback 和 ReturnCallback
        template.setConfirmCallback(this);
        template.setReturnCallback(this);
    }

    public void sendMsg(String exchange, String key, String msg) {
        CorrelationData data = new CorrelationData();
        data.setId(OrderNoUtils.idStr());
        template.convertAndSend(exchange, key, msg, data);
    }


    @Override
    public void confirm(CorrelationData data, boolean ack, String cause) {
        log.info("confirm is {} ack {} cause {}", data, ack, cause);
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String key) {
        log.info("return message {} replyCode {} replyText {} exchange {} key {}", message, replyCode, replyText, exchange, key);
    }
}
