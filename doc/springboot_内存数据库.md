

#### Derby 内存数据库

在 Spring Boot 中使用内嵌式 Derby 数据库是一种轻量级且无需独立数据库服务的解决方案，特别适合开发测试、演示程序或资源受限环境。

```
 <!-- Derby 内嵌数据库 -->
    <dependency>
        <groupId>org.apache.derby</groupId>
        <artifactId>derby</artifactId>
        <scope>runtime</scope>  <!-- 建议设为 runtime，避免编译期冲突 -->
    </dependency>
```

配置文件
1 create=true：数据库不存在时自动创建。
2 数据库路径：未指定路径时（如 testdb），默认创建在项目根目录；绝对路径示例：jdbc:derby:/path/to/db;create=true
3 内存模式：URL 中添加 memory:true（如 jdbc:derby:memory:testdb;create=true），数据仅在应用运行期间存在


```properties

# application.properties
spring.datasource.driver-class-name=org.apache.derby.jdbc.EmbeddedDriver
spring.datasource.url=jdbc:derby:testdb;create=true  # testdb 为数据库名，自动创建
spring.datasource.username=sa  # 默认用户名
spring.datasource.password=    # 密码可为空
# 初始化脚本配置（可选）
spring.datasource.schema=classpath:schema.sql  # 建表脚本
spring.datasource.data=classpath:data.sql      # 初始数据脚本
spring.datasource.initialization-mode=always   # 始终初始化

# 日志输出控制
logging.level.org.apache.derby=info
# 配置网络模式
# spring.datasource.url=jdbc:derby://localhost:1527/testdb;create=true


```

客户端和服务端的交互分为两种：推（Push）和拉（Pull）, 客户端 pull 的基础上添加了长轮询来进行配置的动态刷新。
服务端推送

分库分表策略

标准分片策略 复合分片策略 行表达式分片 Hint强制路由 不分片策略

垂直拆分和水平拆分

1 = in  精确分片算法 PreciseShardingAlgorithm
2 > < between 范围分片算法 RangeShardingAlgorithm
3 复合策略 复合分片算法 ComplexKeysShardingAlgorithm


Feign第一次调用耗时，Ribbon是懒加载机制，第一次调用时，会触发Ribbon的加载过程（从服务注册中心获取服务列表，连接池）

使用拦截器传递认证信息 RequestInterceptor
Ribbon是Netflix开源的一个客户端负载均衡器, [加权]轮询、[加权]随机 hash 最少连接

服务熔断和服务降级
请求缓存 请求合并

Sentinel使用滑动窗口限流算法来实现限流。

springcloud gateway 
kong
断言 路由 过滤器
netty 服务器 predicate -> route -> filter

skywalking
分布式事务
AT TCC 
AT模式是Seata默认支持的模式
