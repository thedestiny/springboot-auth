flink 批量、实时分析、处理调度数据流
低延迟 高吞吐量 流式处理

exactly-once


1.19
flink-java flink sdk 核心包
flink-streaming-java flink-java 流式处理
flink-clients 内嵌的flink的客户端
flink-connector-base flink 的连接器，基础代码
flink-connector-kafka 操作kafka 数据连接

kafka 核心概念
主题 topics 分区 partitions 副本 replicas 
生产者 producers 消费者 consumers 
broker 集群 zookeeper
偏移量 offset 提交 commit 

flink 核心概念
数据流 dataStream 转换 transformations  
窗口 windows 状态 state 
检查的 checkpoints 保存点 savepoints 
时间语义 event time processing time 
stream processing 
batch processing

https://blog.csdn.net/2501_91483426/article/details/155328171


flink 无边界计算和无边界计算 有状态和无状态计算。

实时计算框架 storm spark flink 

kafka rabbitmq rocketmq  处理完数据 发送到mysql rocketmq redis 

jobManager taskManager 
sources -> flatmap -> keyAggregation&Sink
算子 算子链 并行度

flink 客户端

sql 
tableApi
stream table 

dataStream dataSet  流批一体
watermark 延迟时间 事件时间

window 机制

flink sql

exactly-once 
chandy-lamport 算法
jobManager 触发 checkpoint, 向source 注入 barrier 
barrier 随数据流流动，算子对齐 barrier 后快照状态， 全节点完成后快照标记为完成。
两阶段提交
开启事务数据写入 kafka 但是未提交，等待 checkpoint 完成时提交

key 数据倾斜的问题 C端一般不会，B端会有倾斜
增加并行度解决数据倾斜问题 
backpressure 背压 生产速度大于下游消费速度

1 场景 topN
2 维表关联优化



加盐方式解决数据倾斜


无界 实时流处理 
有界 离线批处理

微批处理 spark stream 实时计算
事件驱动 storm flink  真实时计算  时效高 吞吐量低 
流批一体 etl 

playground 安装flink集群

》 ctl +shift + p 创建工程项目
flink 1.17.2


data-warehouse-learning

解压文件
tar -zxvf flink-1.18.1-bin-scala_2.12.tgz -C /opt/software


```
firewall-cmd --reload

cat > docker-compose.yml <<-'EOF'
version: '3'
services:
  zookeeper:
    image: wurstemeister/zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    networks:
      - kafka-net  
  kafka:
    image: wurstemeister/kafka 
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
         
EOF



```





