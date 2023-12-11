package com.platform.messsage.config;

import com.platform.messsage.common.AppConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * direct 定向模式
 * fanout 广播模式
 * topic  通配符模式
 * @Description
 * @Date 2023-12-08 11:12 AM
 */

@Slf4j
@Configuration
public class RabbitmqConfig {

    // direct 模式
    @Bean
    public Queue directQueue() {
        // durable:是否持久化,默认是 true,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue(AppConstant.DIRECT_QUEUE, true, false, false);
    }
    @Bean
    public DirectExchange directExchange() {
        // durable autoDelete
        return new DirectExchange(AppConstant.DIRECT_EXCHANGE, true, false);
    }
    @Bean
    public Binding directBinding() {
        // 根据路由键绑定队列和交换机
        return BindingBuilder.bind(directQueue()).to(directExchange()).with(AppConstant.DIRECT_ROUTE_KEY);
    }
    // topic 模式
    @Bean
    public Queue topicQueueMall() { return new Queue(AppConstant.TOPIC_QUEUE_MALL); }
    @Bean
    public Queue topicQueuePhone() { return new Queue(AppConstant.TOPIC_QUEUE_PHONE); }
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(AppConstant.TOPIC_EXCHANGE, true, false);
    }
    @Bean
    public Binding topicBindingAll() {
        // 根据路由键绑定队列和交换机
        return BindingBuilder.bind(topicQueueMall()).to(topicExchange()).with(AppConstant.TOPIC_QUEUE_ALL);
    }
    @Bean
    public Binding topicBindingPhone() {
        // 根据路由键绑定队列和交换机
        return BindingBuilder.bind(topicQueuePhone()).to(topicExchange()).with(AppConstant.TOPIC_QUEUE_PHONE);
    }
    // fanout 模式 扇型交换机, 不需要配置路由键,配置也不起作用
    @Bean
    public Queue fanoutQueueA() {
        return new Queue(AppConstant.FANOUT_QUEUE_A);
    }
    @Bean
    public Queue fanoutQueueB() {
        return new Queue(AppConstant.FANOUT_QUEUE_B);
    }
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(AppConstant.FANOUT_EXCHANGE, true, false);
    }
    @Bean
    public Binding fanoutBindingA(){
        return BindingBuilder.bind(fanoutQueueA()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBindingB(){
        return BindingBuilder.bind(fanoutQueueB()).to(fanoutExchange());
    }




}
