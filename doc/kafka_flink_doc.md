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





