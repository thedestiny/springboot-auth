
Arthas 使用教程


```

Arthas使用教程(8大分类)
https://blog.csdn.net/lydms/article/details/125238249

https://blog.csdn.net/weixin_37703281/article/details/148394903?spm=1001.2014.3001.5502
# 1秒接入生产环境
curl -O https://arthas.aliyun.com/arthas-boot.jar
java -jar arthas-boot.jar

connection-timeout
read-timeout
# 1. 追踪方法内部调用路径 耗时超时问题
trace com.example.OrderService getOrderById '#cost>1000' -n 5

# 查看方法的入参 属性 返回值 
watch demo.MathGame primeFactors "{params,target,returnObj}" -x 2 -b -s -n 2

# 1. 查看线程状态分布
thread -b # 显示阻塞线程
thread --state WAITING # 等待状态的线程
# 2. 监控锁竞争情况
watch java.util.concurrent.locks.ReentrantLock getQueueLength

# 1. 监控堆内存对象
dashboard -i 5000 # 5秒刷新一次

# 2. 追踪对象创建路径
vmtool --action getInstances --className LoginDTO --limit 10


trace com.example.namespaces methodName


```
