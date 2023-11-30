package com.platform.flex.config;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.Charset;

/**
 * https://mp.weixin.qq.com/s?__biz=Mzg5MDczNDI0Nw==&mid=2247506546&idx=1&sn=88c957a00d0d21147a76611f46f497db&chksm=cfda89baf8ad00ac259d309719997b6be4839f25c4600bc32d8c235dd9dd2e82cfab03aac27d&cur_album_id=3184160945112154113&scene=189#wechat_redirect
 *
 * https://developer.aliyun.com/article/1238384
 * @Description
 * @Date 2023-11-30 4:19 PM
 */

@Configuration
public class RocketProducerConfig {


    public static void main(String[] args) throws Exception {

        DefaultMQProducer producer = new DefaultMQProducer("producer-group");
        producer.setNamesrvAddr("localhost:9876");
        producer.start();

        Message msg = new Message("topic", "消息内容".getBytes(Charset.forName("UTF-8")));
        producer.send(msg);

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer-group");


    }


}
