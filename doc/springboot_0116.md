##### springboot 扩展点，设计容器的启动流程


```

https://mp.weixin.qq.com/s/aWd19GjNKIK5gAPc-UK6oA

1 应用初始化
2 环境准备
3 容器创建和准备
4 刷新容器
5 后置处理

模板方法模式 观察者模式 策略模式

网关的作用  负载均衡 限流 鉴权 路由 灰度 
应用层
网关层
业务-身份-研发
服务层 
组件层 
基础设施 mysql redis es mq git
聚合层
业务中台


应对高并发 缓存 限流 降级
cacheSide dcl 都是属于懒加载
srp 单一职责

工厂模式 创建型
策略模式 行为型

策略模式+模板模式+策略上下文

aop 
日志记录 限流 参数校验
观察者模式

kafka Rebalance 的触发本质是 「消费组的状态一致性被打破」，具体可分为三大类触发场景，覆盖「成员变化、元数据变化、配置变化








CommandLineRunner 和 ApplicationRunner 接口，用于在应用启动完成后执行自定义逻辑。传递的参数不同，ApplicationArguments 和 String 数组。调用外部程序，执行脚本
@EventListener 监听特定的启动事件
ApplicationListener 实现接口监听事件 run
SmartInitializingSingleton 所有的bean 初始化完成之后 afterSingletonsInstantiated
PostConstruct 注解，在 bean 初始化完成后调用，用于执行一些初始化逻辑。
SpringApplicationRunListener 贯穿整个启动过程
BeanPostProcessor bean 的初始化前后
ApplicationContextInitializer 应用上下文初始化器，在应用上下文创建完成后，调用 initialize 方法，用于初始化应用上下文。
InitializingBean 接口，在 bean 初始化完成后调用，用于执行一些初始化逻辑。 afterPropertiesSet





互联网三高设计
高并发 高可用 高扩展   

mysql 数据库 cpu 高问题排查
监控工具、top命令查看
1 应用数据库连接池设置不合理，大量短连接创建和销毁，会造成数据库创建线程和销毁，会造成大量的线程切换，拉高cpu
2 长事务堆积，造成undo log 无法清零，清理线程压力大，mvcc版本链过长，查询需要遍历更多版本，造成事务阻塞，引发其它连锁反应。
3 锁竞争和死锁开销，innodb 的锁检测机制也是需要消耗过多资源。
计算做的太多、sql 没有命中索引被迫走全表扫描和排序，创建大量临时表空间、系统被长事务拖垮
活跃的线程，连接，show processlist;


ShardingSphere

大表治理
分库分表 数据库分区 数据库分库分表  读写分离
冷热分离与数据归档
索引优化 索引重建
大表添加字段
数据迁移

清空表空间间隙 
optimize table your_table_name;
修改表索引， alter_table 重新组织数据
ALTER TABLE your_table ENGINE=InnoDB;

大表添加字段 5.6 之前会锁表 
CREATE TABLE new_table LIKE old_table;
INSERT INTO new_table SELECT * FROM old_table;
DROP TABLE old_table;
RENAME TABLE new_table TO old_table;

锁表 主从延迟 io 磁盘压力过大 读写超时

ddl 工具 gh-ost pt-osc 在线ddl同步工具
创建新表 同步旧表数据 删除旧表 rename 新表


使用扩展表 冗余字段



修改表索引， alter_table 重新组织数据
ALTER TABLE your_table ENGINE=InnoDB;


mysql partition by range


mq 应用解耦 异步非阻塞处理，提高响应速度和吞吐量 负载均衡  削峰填谷 

对象创建过程
常量池是否有此类的引用，没有需要执行类加载
分配类型 栈上分配 tlab 堆上

threadPoolExecutor 

线程池 处理异步任务
异步任务  方法添加注解
异步处理事件 applicationEventMuliCaster，配置线程池或者添加 Async 注解，并设置线程池，默认使用的线程池并非真正异步，SimpleAsyncTaskExecutor，无法复用线程
定时任务处理设置线程池 
监听器同步执行，需要转异步 监听器是同步执行 

aop 
jdk 动态代理 MethodInterceptor
cglib 动态代理 Advisor=Pointcut+Advice

BeanNameAutoProxyCreator 基于 bean 名称的自动代理创建器，用于为指定的 bean 名称创建代理对象。

高可用
https://blog.csdn.net/IOIO_/article/details/155678655


# 
server.tomcat.uri-encoding = UTF-8
#tomcat最小线程数
server.tomcat.min-spare-threads = 200
#tomcat最大线程数
server.tomcat.max-threads = 1000
server.connection-timeout = 60000


#feign超时时间
ribbon.ConnectTimeout = 60000
ribbon.ReadTimeout = 60000
#开启压缩
feign.compression.request.enabled = true
feign.compression.response.enabled = true
#压缩类型
feign.compression.request.mime-types = text/xml,application/xml,application/json
#启用 httpclient
feign.httpclient.enabled = false
feign.okhttp.enabled = true

# 最大连接数
ribbon.MaxTotalConnections = 2000
# 每个host最大连接数
ribbon.MaxConnectionsPerHost = 500
# 最大连接数
feign.httpclient.max-connections = 2000
# 每条路由的最大连接数
feign.httpclient.max-connections-per-route = 500

连接失败
长尾延迟 

# https://mp.weixin.qq.com/s/WDHnCDNVTM9k0cnzqcFqcg
长窗低频（长窗口 + 低频次） 1h/7h/24h 5-10次
短窗高频（短窗口 + 高频次） 1s 100-1000次
限流 滑动窗口 漏斗 令牌桶

长窗低频重准确性与持久性，需全局一致、长期存储；
短窗高频重吞吐与响应速度，可牺牲部分精度换取性能。

mysql max_allowed_packet =4M
查询语句过长，oom, 全表扫描
若IN列表匹配的行数超过表行数的5%~10%，优化器直接放弃索引，选全表扫描；
大量数据查询，走临时表的方法
大表数据查询
https://mp.weixin.qq.com/s/nH_U9kpIJARiL5XhNdINPw
线程数优化
https://mp.weixin.qq.com/s/rdEXjrkh5-kqBSv_qam3Zw

redis 内存过高
使用 keys 命令 热点key key的value过大
使用 hgetall 命令查看热点key的value

响应时间 并发数 吞吐量
访问对象的方式 句柄和直接指针


AQS是多线程同步器，它是J.U.C包中多个组件的底层实现，如Lock、CountDownLatch、Semaphore等都用到了AQS.
fail-safe 失败安全
fail-fast 快速失败
ConcerrentHashMap和CopyOnWriteArrayList
静态变量、成员变量、静态代码





```

##### transactional 失效的场景

1 访问权限问题,如果不是 public 方法,不支持事务
2 final 修饰的方法,spring 事务底层使用了 aop,使用 jdk 动态代理或者cglib生成代理类，final 方法无法被重写，没有办法添加事务功能
3 方法的内部调用，导致事务不生效 使用 AopContext.currentProxy() 获取代理对象
4 未被 spring 处理
5 多线程调用
6 数据库表不支持事务
7 没有开启事务操作
8 方法内异常信息没有正常抛出
9 事务传播属性设置错误，
required supports mandatory requires_new not_supported never nested 
10 spring 默认回滚 runtimeException error 对于非运行时异常，不会回滚
设置 rollbackFor

大事务操作会导致死锁 回滚时间长 接口超时 主从延迟 数据库连接池沾满 锁等待
网络io 文件io 

应用层使用的注解
@transactional

PlatformTransactionManager 事务管理器 DataSourceTransactionManager
DataSource 资源层
TransactionDefinition 事务配置
TransactionStatus 事务状态

权限问题 代理机制导致 异常类型
数据库连接池配置 
最小连接 cpu*2 
最大连接 cpu*10 

线程的6大状态
new-ready-running-terminate
waiting-blocked
time-waiting
线程池的状态
running shutdown stop tidying terminated


daemon 
守护线程是在后台运行，程序终止时退出守护线程终止
用户线程是 用户创建，任务结束时线程结束

collection map 
set list queue  
hashmap 会出现死循环和数据覆盖的问题，
数组+链表
优化有改为数组+链表+红黑树
头插法和尾插法

java 并发集合
java内存模型 JMM
java并发基础
java锁
并发工具类
其它线程工具类
java并发集合
原子类atomic
阻塞队列
线程池

juc
Synchronized



1 负责龙湖集团会员账户体系的创建与维护，涵盖积分发放、消费管理、清算结算及积分有效期处理，确保系统功能的稳定性和高可用。
2 参与账户体系重构，设计并实施新旧系统数据迁移方案，实现系统不停机切换，确保业务连续性。
3 从零搭建订单中心、交易中心，实现珑珠生活的订单和交易分离，提高系统的性能。
4 参与集团技术任务，推动服务部署容器化，MQ消息上云，服务升级至jdk17,日志脱敏，旧系统下线等多项任务，优化系统架构和运维效率。



