
#### Mybatis Plus + P6Spy


1 实现Mybatis Plus + P6Spy 进行sql语句输出
2 实现Mybatis Plus + P6Spy 进行sql执行耗时输出


```
p6spy 是一个可以用来在应用程序中拦截和修改数据操作语句的开源框架。通常使用它来跟踪数据库操作，查看程序运行过程中执行的sql语句。简单来说，就是对 SQL 语句执行过程的监控。我们一般用来做两件事情：
1 打印完整的sql语句
2 sql语句的耗时
https://p6spy.readthedocs.io/en/latest/
```

集成步骤如下:
##### 1 加入依赖
```
<dependency>
    <groupId>p6spy</groupId>
    <artifactId>p6spy</artifactId>
    <version>3.9.1</version>
</dependency>
```
#### 2 数据库链接配置修改
```
spring:
  datasource:
   driver-class-name: com.p6spy.engine.spy.P6SpyDriver
   url: jdbc:p6spy://localhost:3306/springboot-demo?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Shanghai
   username: root
   password: 23456
```

#### 3 在resources目录下创建spy.properties属性文件
```
# P6Spy的配置
# 应用的拦截模块
modulelist=com.baomidou.mybatisplus.extension.p6spy.MybatisPlusLogFactory,com.p6spy.engine.outage.P6OutageFactory
# 自定义日志打印
logMessageFormat=com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger
# 日志输出到控制台
appender=com.baomidou.mybatisplus.extension.p6spy.StdoutLogger
# 使用日志系统记录 sql
#appender=com.p6spy.engine.spy.appender.Slf4JLogger
# 设置 p6spy driver 代理
deregisterdrivers=true
# 取消JDBC URL前缀
useprefix=true
# 配置记录 Log 例外,可去掉的结果集有error,info,batch,debug,statement,commit,rollback,result,resultset.
excludecategories=info,debug,result,commit,resultset
# 日期格式
dateformat=yyyy-MM-dd HH:mm:ss
# 实际驱动 可多个
driverlist=com.mysql.cj.jdbc.Driver
# 是否开启慢SQL记录
outagedetection=true
# 慢SQL记录标准 2 秒
outagedetectioninterval=2
# 是否过滤 Log
filter=true
# 过滤 Log 时所排除的 sql 关键字，以逗号分隔
exclude=select 1 from dual
```
