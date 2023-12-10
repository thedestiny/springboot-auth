package com.platform.messsage.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AppSendMsg implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private RabbitTemplate template;

    public void sendMsg(String exchange, String key, String data){
        template.convertAndSend(exchange, key, data);
    }


    @Override
    public void confirm(CorrelationData data, boolean ack, String cause) {
        log.info("confirm is {} ack {} cause {}", data, ack, cause);
    }
}
