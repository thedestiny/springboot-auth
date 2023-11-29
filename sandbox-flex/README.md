

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

消息发送超时、响应超时会进行两次重试操作，默认是轮询发送消息
发送时消息重发
消费时消息重复消费，所以接口要实现幂等
消息集合条数为 1 
Queue存的是每个消息的位置， commitlog 存储的是消息本体
消息消费时，需要提交消息的 offset 
消费完消息的时候并不是同步告诉RocketMQ服务端offset，而是定时发送,默认 5秒发送一次。
broker 默认 5s 将内存中的消费进度持久化到磁盘文件中
在主从同步模式下，从节点默认每隔10s会向主节点发送请求
集群消费的实现就是将队列按照一定的算法分配给消费者，默认是按照平均分配的。
消费者重平衡


顺序消息
在某些业务场景下，消息需要顺序发送和消费才具有意义和价值。
消息错乱的原因
producer 发送时是轮询发送消息，如果发送到不同的分区队列，就不能保证FIFO了。
consumer 是多线程并发消费同一个 messagequeue 即使消息是顺序到达的，也不能保证消息顺序消费

核心需要消息发送顺序同步发送，可以保证发送到统一个队列 queue

全局有序和分区有序
全局有序 是所有的消息都严格按照先进先出进行生产和消费，一个topic 只有一个分区，consumer 也只有一个线程消费，应用于性能要求低的场景中，
分区有序 根据某个shardkey 进行分区，保证关联业务的顺序性。




```
