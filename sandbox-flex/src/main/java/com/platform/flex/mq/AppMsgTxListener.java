package com.platform.flex.mq;


import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;


/**
 * 消费事务消息
 */
@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "springboot_consumer_group",
        topic="tx-message",
        selectorExpression = "*",
        messageModel = MessageModel.CLUSTERING
)
public class AppMsgTxListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {

        log.info("tx message {} ", message);
    }
}
