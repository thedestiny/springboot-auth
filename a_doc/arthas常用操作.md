
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

```

仪表盘
刷新实时数据，间隔为2s, 刷新2次
dashboard -i 2000 -n 2

线程信息
thread  #显示第一页线程信息
thread --all 显示所有线程信息
thread id   #显示对应线程id的堆栈信息，id为上面显示的ID列的值。


thread -n 3 #查看最忙的三个线程堆栈信息
thead -b  #查看当前java进程是否存在死锁

Search Method，搜索jvm已经加载的方法信息
sm -d 全限定类名,方法名，即全路径

Search Class，搜索jvm已经加载的类信息
sc -d 全限定类名  #显示对应类的详细信息

反编译类信息成为 java 代码
jad demo.MathGame


作用是观察方法的入参、返回值、异常、实例对象本身等信息。
watch 全限定类名 方法名 【观察表达式】

# 观察两个层级
watch demo.MathGame primeFactors '{params, target, returnObj, throwExp}' -x 2 







```
