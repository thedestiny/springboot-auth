package com.platform.flex.mq;

// import org.apache.rocketmq.client.consumer.listener.MessageListener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * https://blog.csdn.net/m0_49183244/article/details/129169326
 * https://blog.csdn.net/m0_49183244/article/details/128147349
 * https://blog.csdn.net/weixin_41953346/article/details/127324629
 *
 *
 */

/**
 * 默认为集群消息，广播消息所有的消费者都能收到，一般用于配置的广播
 */
@Slf4j
@RocketMQMessageListener(
        consumerGroup = "app-auth-group",
        topic="app-auth-filter",
        selectorType = SelectorType.SQL92,
        selectorExpression = "a between 6 and 9",
        messageModel = MessageModel.CLUSTERING
)
public class AppMsgFilterListener implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        log.info("message {} ", message);
    }
}
