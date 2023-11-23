

mybatis-flex 配置
数据的新增和修改、删除
数据的查询
分页查询
where 条件
setRow


```

# https://mp.weixin.qq.com/s/TdAJifGZwg-Av9sgA7kO-A
rocketmq 消息类型 同步 异步 单向

普通消息 顺序消息 普通消息 集群消息  延迟消息 事务消息 
消息重试 死信

并发消息 顺序消费


生产者间隔 30秒从 nameserver 拉取最新的路由信息并更新本地缓存

rocketmq 采用了两种消息队列投递算法 轮询算法 和最小投递延迟算法
producer.setSendLatencyFaultEnable(true);
重试时间间隔
producer.setRetryTimesWhenSendFailed(10);

自定义消息队列选择算法 MessageQueueSelector
随机算法
Hash算法
根据机房选择算法

消息大小超过4k就会先压缩，再发送

# 默认是集群消息，可以设置为广播消息
consumer.setMessageModel(MessageModel.BROADCASTING);

msgId由producter端生成，其生成的规则；
producerId + 进程pid + messageClientIDsetter类的classLoader的hashcode + 当前时间 + AutomicInteger自增计数器
offsetMsgId：由broker端生成。

一个 topic 默认 4个 queue 
消息位置  消息长度  tag  hashcode 
8 + 8 + 4


```
