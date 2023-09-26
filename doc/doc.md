
##### BigDecimal 舍入模式
```
1 ROUND_UP
向远离零的方向 
1.1 -> 2.0 
1.0 -> 1.0
1.6 -> 2.0
-1.1 -> -2

2 ROUND_DOWN
向接近零的方向取整

```


```
@JsonInclude注解 

表示全部序列化
@JsonInclude(JsonInclude.Include.ALWAYS)
非空字段序列化
@JsonInclude(JsonInclude.Include.NON_NULL)
非 null 值序列化
@JsonInclude(JsonInclude.Include.NON_EMPTY)



```


```

https://zhuanlan.zhihu.com/p/594372000

热点账户根据资金的方向可以分为入账和出账,有可能是出账频繁，也可能是入账频繁
常见的收单，电商商户，第三方支付公司等都可以理解为入账频繁的账户
出账频繁的账户是一些扣费类的场景，比如提供短信服务、广告计费服务、流量扣费类的账户，这些账户是一般是按照频次、流量收费，一般都是小额交易。

除此之外，还有一种入账和出账都频繁的账户，

热点账户的优化策略

限流的方式，当请求超过系统的承载能力后选择丢弃，本质上是将技术问题转换为业务问题，牺牲了一部分的业务体验，保障业务系统的正常运行。
缓冲的方式，本质是削峰填谷，将一部分的业务请求编程异步处理，用于平抑业务处理的压力，能够以恒定的速率去执行请求。
可以使用消息队列、异步线程池的方式来处理，这些是入账实时性不高的业务场景，适应于账户增加的余额的场景，对于扣减的场景并不适用。

汇总的方式，该方式是将交易记录记录在数据库表中，并不直接更新账户余额，而是以一定周期的方式进行汇总，这种处理方式在首单的场景下比较常见
通常是 T+1, 入账多，出账少，可以避免账户的透支。

缓存的方式，将账户余额存入数据库中保存，处理用户请求的同时需要记录请求单号，用于后续的对账操作。

分治的方式，将商户分为多个子账户，每个账户中的余额足够，通过负载均衡算法将请求分到每一个子账户中，这样可以提高账户的处理速度和性能，


限流方式
消息队列方式
汇总方式
缓存
分治







```



```

https://blog.csdn.net/wang7075202/article/details/111308905

segment file 
我们都知道 ES 的存储基本单元为 shard , 一个 Index 中有多个 Shard, ES 是基于 Lucence 的 Index, 并且每个 Index 是由多个 Segment 组成的
每个 Segment 都是一些倒排索引的集合，每次创建一个文档 Document ,都会归属到一个新的 Seqgment, 而不是去修改原来的 Segment。每次文档的删除只是将文档添加删除标记
而不是真正的物理删除。ES 的 index 是一个逻辑的抽象概念，ES 每秒都会生成一个新的 Segment文件，当 Semennt 文件过多时会进行合并 merge操作,合并时会将标识为删除的文档物理删除。

commit , 这个适合数据库事务类似的，也是为了数据的安全，每次索引变更都会立即刷盘，这个动作会将 Segment 进行合并并写盘，保证内存中的数据尽量不丢失。
刷盘 flush 是很重的io 操作，为了保证机器的性能并保证搜索的近实时性，通常 flush 操作不会那么频繁。


translog translog 提供所有还没有刷到磁盘的操作的一个持久化记录。在 ES 启动时，它会从磁盘中使用最后一个提交点去恢复已知的 Segment, 并且重放 translog 中所有的变更操作，，
为了方式 es 宕机所造成的的数据丢失，保证数据的可靠性。es 的每次操作不仅要写入内存 buffer ,还会呗写入到 translog 文件，每个 shard 都对应一个 translog , translog 
间隔 5s 会异步执行或者每个请求完成后执行一次 fsync 操作，将 translog 从缓存写入磁盘，这样的操作比较耗时，如果对数据实时性要求不高可以改为 async 方式，否则节点宕机会有 5s 的数据丢失。


refresh  写入和打开一个新的 segment 的轻量过程， es 接收数据请求时先存入内存中，默认间隔 1s 会从内存 buffer 中将数据写入 filesystem cache 中的一个 segment, 内存中的 buffer 被清空，
这个时候数据就变成可搜索的，这个过程称之为 refresh .

fsync fsync 是 linux 系统的调用函数，是将内存 buffer 中的数据存储到文件系统中， 这里是将 filesystem cache 中的所有 segment 刷新到磁盘的操作；

flush es 间隔 30 min 或者数据量达到 512MB 时，会将内存 buffer 数据全部写入新的 segment 中，内存 buffer 被清空，一个 commit point 被写入磁盘，
并将 filesystem cache 中的数据通过 fsync 刷入磁盘，同时清空 translog 日志文件，这个过程叫做 flush；

近实时搜索

提交（Commit）一个新的 segment 到磁盘需要一个 fsync 来确保 segment 被物理性地写入磁盘，这样在断电的时候就不会丢失数据。






```
