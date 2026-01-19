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




大表治理
分库分表 数据库分区 数据库分库分表  读写分离
冷热分离与数据归档
索引优化 索引重建
大表添加字段
数据迁移

清空表空间间隙 
optimize table your_table_name;

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
异步处理事件 applicationEventMuliCaster
定时任务处理设置线程池 
监听器同步执行，需要转异步 监听器是同步执行 



```
