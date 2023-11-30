package com.platform.flex.mq;

// import org.apache.rocketmq.client.consumer.listener.MessageListener;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;


/**
 * 事务消息 本地监听，用于消息发送成功后的处理以及事务状态的检查
 */
@Slf4j
@RocketMQTransactionListener
public class AppMsgTxListener implements RocketMQLocalTransactionListener {

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        MessageHeaders headers = msg.getHeaders();
        String txId = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        String keys = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.KEYS);
        log.info("执行本地事务 id {} and key {}", txId, keys);
        // todo business 根据业务执行情况提交事务状态
        return RocketMQLocalTransactionState.COMMIT;
        // return RocketMQLocalTransactionState.UNKNOWN;

    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        MessageHeaders headers = msg.getHeaders();
        String txId = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.TRANSACTION_ID);
        String keys = (String) headers.get(RocketMQHeaders.PREFIX + RocketMQHeaders.KEYS);
        log.info("检查本地事务 id {} and key {}", txId, keys);

        // todo business 查询业务执行情况，提交或者回滚信息
        return RocketMQLocalTransactionState.COMMIT;
        // return RocketMQLocalTransactionState.ROLLBACK;
        // return RocketMQLocalTransactionState.UNKNOWN;
    }
}
