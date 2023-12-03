package com.platform.flex.mq;


import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 *
 * messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
 *
 * 消息消费
 * @Description
 * @Date 2023-12-02 11:34 PM
 */
public class AppMessageListener implements MessageListenerConcurrently {


    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {


        // return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
