package com.platform.flex.mq;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.platform.flex.dto.StudentDto;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.Map;

/**
 * 消息发送
 *
 * @Date 2023-11-28 4:19 PM
 */

@Configuration
public class MsgProvider {


    @Autowired
    private RocketMQTemplate template;

    public void sendMsgConfig(){

        template.setCharset("UTF-8");
        // 配置 MessageQueueSelector
        template.setMessageQueueSelector(((mqs, msg, key) -> {
            int idx = key.hashCode() % mqs.size();
            return mqs.get(idx);
        }));
    }

    public void sendOrderMessage(StudentDto msg){

        // 8 顺序消息， 同步发送消息
        SendResult orderId1 = template.syncSendOrderly("app-auth-order", msg, String.valueOf(msg.getId()));
    }


    public void sendTxMessage(){

        String msg = "事务消息";
        Message<String> builder = MessageBuilder.withPayload(msg).setHeader(RocketMQHeaders.KEYS,3 ).build();
        TransactionSendResult result = template.sendMessageInTransaction("tx-message", builder, "ext-params");
    }

    /**
     * 消息发送
     */
    public void sendMessage() {

        String topicAndTag = "order-notice:finish";
        JSONObject body = new JSONObject();
        body.put("code", "100");
        body.put("msg", "成功");
        String msg = body.toJSONString();

        // 1 普通消息，同步发送
        template.convertAndSend(topicAndTag, msg);
        // 2 同步消息，返回发送结果
        SendResult sendResult = template.syncSend(topicAndTag, msg);
        // 3 延迟消息, 发送消息超时 100ms, 消息延迟级别
        Message<String> builder = MessageBuilder.withPayload(msg).build();
        template.syncSend(topicAndTag, builder, 100, 3);
        // 4 批量消息
        template.syncSend(topicAndTag, Lists.newArrayList(builder), 200);
        // 5 异步消息
        template.asyncSend(topicAndTag, msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("消息处理成功！");
            }

            @Override
            public void onException(Throwable e) {
                System.out.println("消息处理失败！");
            }
        });
        // 6 单向消息
        template.sendOneWay(topicAndTag, body.toString());
        // 7 过滤消息
        Map<String, Object> headers = Maps.newHashMap();
        headers.put("a", "7");
        template.convertAndSend(topicAndTag, msg, headers);
        // 8 顺序消息， 同步发送消息
        SendResult orderId1 = template.syncSendOrderly(topicAndTag, msg, "orderId");
        // 异步发送消息
        template.asyncSendOrderly(topicAndTag, msg, "orderId", new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
            }

            @Override
            public void onException(Throwable e) {
            }
        });
        // 单向发送消息
        template.sendOneWayOrderly(topicAndTag, msg, "orderId");

        // 9 事务消息


    }

}
