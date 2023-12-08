package com.platform.messsage.config;

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


    @Bean
    public Queue directQueue() {
        // durable:是否持久化,默认是 true,持久化队列：会被存储在磁盘上，当消息代理重启时仍然存在，暂存队列：当前连接有效
        // exclusive:默认也是false，只能被当前创建的连接使用，而且当连接关闭后队列即被删除。此参考优先级高于durable
        // autoDelete:是否自动删除，当没有生产者或者消费者使用此队列，该队列会自动删除。
        return new Queue("direct-queue", true, false, false);
    }

    @Bean
    public DirectExchange directExchange() {
        // durable
        // autoDelete
        return new DirectExchange("direct-exchange", true, false);
    }

    @Bean
    public Binding directBinding() {
        // 根据路由键绑定队列和交换机
        return BindingBuilder.bind(directQueue()).to(directExchange()).with("direct-route-key");
    }


    @Bean
    public Queue topicQueue() {
        return new Queue("topic-queue");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("topic-exchange", true, false);
    }

    @Bean
    public Binding topicBinding() {
        // 根据路由键绑定队列和交换机
        return BindingBuilder.bind(topicQueue()).to(topicExchange()).with("topic.#");
    }

    // 扇型交换机, 不需要配置路由键,配置也不起作用
    @Bean
    public Queue fanoutQueue() {
        return new Queue("fanout-queue");
    }
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("topic-exchange", true, false);
    }

    @Bean
    public Binding fanoutBinding(){
        return BindingBuilder.bind(fanoutQueue()).to(fanoutExchange());
    }





}
