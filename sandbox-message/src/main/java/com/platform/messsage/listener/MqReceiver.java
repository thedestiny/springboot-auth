package com.platform.messsage.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Date 2023-12-08 4:39 PM
 */

@Slf4j
@Component
public class MqReceiver implements ChannelAwareMessageListener {


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        String queue = message.getMessageProperties().getConsumerQueue();
        log.info("queue {} tag {} msg {}", queue, deliveryTag, new String(message.getBody()));
    }


}
