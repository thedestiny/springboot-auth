

```

本地缓存问题
缓存一致性的解决方案
1 mq 消息队列通知业务删除本地缓存节点
2 使用 cannal 订阅数据库变更日志，当数据库发生变化时，再操作缓存。

从易用性角度，Guava Cache、Caffeine和Encache都有十分成熟的接入方案，使用简单。
从功能性角度，Guava Cache和Caffeine功能类似，都是只支持堆内缓存，Encache相比功能更为丰富
从性能上进行比较，Caffeine最优、GuavaCache次之，Encache最差(下图是三者的性能对比结果）


```
