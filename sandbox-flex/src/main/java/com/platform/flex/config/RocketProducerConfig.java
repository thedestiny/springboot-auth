package com.platform.flex.config;

import com.platform.flex.utils.IdGenutils;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

/**
 * https://mp.weixin.qq.com/s?__biz=Mzg5MDczNDI0Nw==&mid=2247506546&idx=1&sn=88c957a00d0d21147a76611f46f497db&chksm=cfda89baf8ad00ac259d309719997b6be4839f25c4600bc32d8c235dd9dd2e82cfab03aac27d&cur_album_id=3184160945112154113&scene=189#wechat_redirect
 * <p>
 * https://developer.aliyun.com/article/1238384
 * https://zhuanlan.zhihu.com/p/585333827
 * https://zhuanlan.zhihu.com/p/585333827
 * <p>
 * <p>
 * 消息重试 分为生产者重试和消费者重试
 * 同步发送和异步发送，均支持消息发送重试， 重试带来的问题是服务端压力变大以及消息重复
 * 最佳实践
 * 1 重试需要合理设置发送超时时间以及消息发送的最大次数
 * 2 如何保证消息发送不丢失
 * <p>
 * 消费者重试
 * 消费者在消费某条消息失败后，ReconsumeLater 服务端会根据重试策略重新向客户端投递该消息
 * 限流的目的是将超出流量的消息暂时堆积在队列中达到削峰的作用
 * maxReconsumeTimes 最大重试次数 ，
 * 非顺序消息重试间隔为阶梯时间，顺序消息固定间隔为 3s
 * 最佳实践
 * 1 消费方应该有一个良好的幂等设计
 * 2 不要用重试机制来进行业务限流
 * 3 设置消费的最大超时时间和次数
 */

@Slf4j
@Configuration
public class RocketProducerConfig {


    public static void main(String[] args) throws Exception {

        // 创建生产者和消费者
        createProducer();
        createConsumer();

    }

    /**
     * 创建消费者
     */
    private static void createConsumer() {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");
        consumer.setNamesrvAddr("localhost:9876");
        consumer.setInstanceName(IdGenutils.idStr());
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.setMessageModel(MessageModel.BROADCASTING);
        // 决定是否使用VIP通道，即高优先级
        consumer.setVipChannelEnabled(false);
        consumer.setConsumeThreadMax(1);
        consumer.setConsumeThreadMin(1);
        consumer.setConsumeMessageBatchMaxSize(2);
        consumer.setConsumeTimeout(15); // 15 分钟消费超时
        try {
            // 订阅 topic 下的所有消息
            consumer.subscribe("rocket-mq-topic", "*");
            // 注册一个消息者监听器
            consumer.registerMessageListener((MessageListenerConcurrently) (list, ctx) -> {
                        for (MessageExt msg : list) {
                            log.info("消费消息: {}", new String(msg.getBody()));
                        }
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
            );

            consumer.start();
            log.info("start message !");

        } catch (Exception e) {

        }
    }

    /**
     * 创建生产者
     */
    private static void createProducer() throws Exception {

        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.setSendMessageWithVIPChannel(false);
        // 超时时间
        producer.setSendMsgTimeout(5000);
        producer.setInstanceName(IdGenutils.idStr());
        // 生产者消息发送失败重试规则
        producer.setSendLatencyFaultEnable(true);
        producer.setRetryTimesWhenSendFailed(2);
        producer.setRetryTimesWhenSendAsyncFailed(2);
        // 启动生产者
        producer.start();
        Message msg = new Message("rocket-mq-topic", "消息内容".getBytes(StandardCharsets.UTF_8));
        // 发送消息
        producer.send(msg);
        log.info("send message {}", msg.toString());


    }


}
