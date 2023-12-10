package com.platform.messsage.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Date 2023-12-08 3:24 PM
 */

@Slf4j
@Component
@RabbitListener(queues = "fanout_queue_a")
public class AppRabbitmqListener {


    @RabbitHandler
    public void process(Message message, Channel channel) throws Exception {

        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            log.info("rabbitmq consume msg {}", message);
            // multiple true:将一次性ack所有小于deliveryTag的消息。
            channel.basicAck(deliveryTag, true);
            //channel.basicReject(deliveryTag, true);
        } catch (Exception e) {
            log.error("error information  {}", e.getMessage(), e);
            // channel.basicNack(deliveryTag, true, true);
        }


    }


}
