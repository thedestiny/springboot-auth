package com.platform.flex.mq;

// import org.apache.rocketmq.client.consumer.listener.MessageListener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * https://blog.csdn.net/m0_49183244/article/details/129169326
 * <p>
 * https://blog.csdn.net/weixin_41953346/article/details/127324629
 */

/**
 * 顺序消息指定消费者组的最大线程数，可以保证消息的顺序消费
 * 默认为集群消息，广播消息所有的消费者都能收到，一般用于配置的广播
 * selectorExpression = "*",
 * selectorType = SelectorType.TAG,
 * consumerGroup = "springboot_consumer_group",
 * messageModel = MessageModel.CLUSTERING
 */
@Slf4j
@Component
@RocketMQMessageListener(
        consumerGroup = "springboot_consumer_group",
        selectorType = SelectorType.TAG,
        topic = "order-order",
        consumeTimeout = 15,
        consumeThreadMax = 20,
        messageModel = MessageModel.CLUSTERING
)
public class AppMsgOrderListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("consume message {} ", message);
    }
}
