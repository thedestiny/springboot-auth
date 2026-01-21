package com.platform.authcommon.config;

import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.Retryer;
import feign.okhttp.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ReactorLoadBalancer;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.cloud.openfeign.FeignClientSpecification;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * openfeign 第一次调用慢
 * 1 关闭懒加载或者初始化客户端调用 @PostConstruct
 * 2 开启日志打印，观察调用链路耗时
 * 3 替换 HTTP 客户端并配置连接池 使用 httpclient 或者 okhttp ,默认使用 urlconnection
 * 4 优化负载均衡器初始化
 * 5 越热核心服务,启动后调用轻量级检测接口
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-11 2:05 PM
 */

@Configuration
public class FeignClientConfig {


    @Autowired
    private List<FeignClientSpecification> specificationList;

//     @Bean
//    public ReactorLoadBalancer<ServiceInstance> reactorLoadBalancer(Environment env,
//                                                                    LoadBalancerClientFactory factory) {
//        String name = env.getProperty(LoadBalancerClientFactory.PROPERTY_NAME);
//        // 提前加载服务列表并缓存
//        return new RoundRobinLoadBalancer(
//                            factory.getLazyProvider(name, ServiceInstanceListSupplier.class), name
//                        );
//    }


    @PostConstruct
    public void initFeignClients() {
         // 手动触发Feign客户端初始化
        FeignClientFactoryBean factory = new FeignClientFactoryBean();
        // 循环初始化所有Feign客户端（具体实现需结合项目的Feign配置）
        for (FeignClientSpecification spec : specificationList) {
//            factory.setBeanFactory(applicationContext);
//            factory.setSpecification(spec);
            factory.getObject(); // 触发初始化
        }
    }

    // 第一步开启所有日志打印
    @Bean
    public Logger.Level feignLoggerLevel() {

        return Logger.Level.FULL; // 打印所有细节日志
    }
    // 第二步 将日志打印到控制台
    //  logging:
    //          level:
    //             com.example.order.feign.PayFeignClient: DEBUG # 你的Feign接口全类名

    // spring.main.allow-bean-definition-overriding: true # 允许覆盖Bean定义
    // spring.main.lazy-initialization: false # 全局禁用懒加载（谨慎！可能增加启动时间
    //

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient(new okhttp3.OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)

                .build());
    }
//
    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder()
                .client(new OkHttpClient()) // 使用OkHttp客户端
                .options(new Request.Options(5000, 10000))
                .logLevel(feignLoggerLevel()) // 设置日志级别
                .requestInterceptor(requestTemplate -> { // 添加请求拦截器，如果需要的话
                    // requestTemplate.header("Header-Name", "Header-Value");
                })
                .retryer(new Retryer.Default(100, TimeUnit.SECONDS.toMillis(1), 3)); // 设置重试策略
    }



}
