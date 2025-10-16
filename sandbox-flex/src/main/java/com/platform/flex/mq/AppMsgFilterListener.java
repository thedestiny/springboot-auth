package com.platform.flex.mq;

// import org.apache.rocketmq.client.consumer.listener.MessageListener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.MessageQueueSelector;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;

import java.util.List;

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
        consumerGroup = "springboot_consumer_group",
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


    /**
     * 发送局部有序消息
     * @param topic
     * @param json
     */
    public void sendMQMessage(String topic, String json) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {

        MQProducer producer = new DefaultMQProducer("namespace");
        Message msg = new Message(topic,"", json.getBytes());

        // 设置超时时间
        producer.send(msg, new MessageQueueSelector() {
            @Override
            public MessageQueue select(List<MessageQueue> list, Message message, Object o) {
                return list.get(0);
            }
        }, new Object(), 2000L);

        producer.shutdown();
    }
}
