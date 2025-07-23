package com.platform.authmpg.config;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @Description 轻量化动态线程池实现方案
 * @Author liangkaiyang
 * @Date 2025-07-23 3:58 PM
 */

@Slf4j
@Component
@Endpoint(id = "appExecutor")
public class AppExecutorEndpoint {


    @Autowired
    @Qualifier("customExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    // 请求路径  /actuator/appExecutor/status 查询线程池状态
    @ReadOperation
    public String status(@Selector String name) {
        log.info("appExecutor status {}", name);
        JSONObject json = new JSONObject();
        json.put("activeCount", taskExecutor.getActiveCount());
        json.put("poolSize", taskExecutor.getPoolSize());
        json.put("queueSize", taskExecutor.getQueueSize());
        return json.toJSONString();
    }


    // 请求路径  /actuator/appExecutor/execute 修改线程池状态
    @WriteOperation
    public String execute(@Selector String name, @Selector Integer queueCapacity) {
        log.info("appExecutor execute {}", name);
        taskExecutor.setQueueCapacity(queueCapacity);
        return "success";
    }


}
