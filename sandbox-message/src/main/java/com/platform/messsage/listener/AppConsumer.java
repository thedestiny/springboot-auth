package com.platform.messsage.listener;


import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.platform.messsage.common.AppConstant;
import com.platform.messsage.dto.MsgDto;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Component
public class AppConsumer {

    private static ObjectMapper mapper;
    private static CollectionType LIST_TYPE;
    private static MapType MAP_TYPE = TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class);

    static {
        LIST_TYPE = TypeFactory.defaultInstance().constructCollectionType(ArrayList.class, MAP_TYPE);
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    // 使用 message channel 方式接收消息，进行参数转换
    @RabbitHandler
    @RabbitListener(queues = "app.queue")
    public void process(Message message, Channel channel) throws Exception {

        long tag = message.getMessageProperties().getDeliveryTag();
        try {
            MsgDto msgDto = mapper.readValue(new String(message.getBody()), MsgDto.class);
            log.info("rabbitmq queue consume msg {} ", JSONObject.toJSONString(msgDto));
            // multiple true:将一次性ack所有小于 tag 的消息。
            channel.basicAck(tag, true);
            //channel.basicReject(deliveryTag, true);
        } catch (Exception e) {
            log.error("error information  {}", e.getMessage(), e);
            channel.basicNack(tag, true, true);
        }
    }

    // 直接使用对象来接收
    @RabbitHandler
    @RabbitListener(queues = AppConstant.DIRECT_QUEUE)
    public void processDirect(MsgDto dto) throws Exception {
        log.info("consume direct message {}", JSONObject.toJSONString(dto));
    }

    @RabbitHandler
    @RabbitListener(queues = AppConstant.TOPIC_QUEUE_PHONE)
    public void processTopicPhone(MsgDto dto) throws Exception {
        log.info("consume topic phone message {}", JSONObject.toJSONString(dto));
    }

    @RabbitHandler
    @RabbitListener(queues = AppConstant.TOPIC_QUEUE_MALL)
    public void processTopicMall(MsgDto dto) throws Exception {
        log.info("consume topic mall message {}", JSONObject.toJSONString(dto));
    }

    @RabbitHandler
    @RabbitListener(queues = AppConstant.FANOUT_QUEUE_A)
    public void processFanoutA(MsgDto dto) throws Exception {
        log.info("consume fanout a message {}", JSONObject.toJSONString(dto));
    }

    @RabbitHandler
    @RabbitListener(queues = AppConstant.FANOUT_QUEUE_B)
    public void processFanoutB(MsgDto dto) throws Exception {
        log.info("consume fanout b message {}", JSONObject.toJSONString(dto));
    }

    // @RabbitHandler
    // @RabbitListener(queues = "app.queue")
    // public void process(MsgDto dto) throws Exception {
    //     log.info("consume app queue message {}", JSONObject.toJSONString(dto));
    // }
}
