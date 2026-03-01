package com.platform.controller;

import com.alibaba.fastjson.JSONObject;
import com.platform.config.Person;
import com.platform.config.UserInfo;
import com.platform.config.UserInfoReq;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-09-19 2:19 PM
 */

@Slf4j
@RestController
@RequestMapping(value = "/api/v1")
public class ArgumentResolverController {


    /**
     * 测试参数解析器
     *
     * @param person
     * @param userInfoReq
     * @return
     */
    @PostMapping("/resolver/test")
    public String parameterResolver(@RequestBody Person person, @UserInfo UserInfoReq userInfoReq) {
        log.info("test person:{}", JSONObject.toJSONString(person));
        log.info("test userInfoReq:{}", JSONObject.toJSONString(userInfoReq));

        TaskQueue taskQueue = new TaskQueue();

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        ThreadPoolExecutor executor1 = new ThreadPoolExecutor(100, 1,
                1, TimeUnit.SECONDS, taskQueue);
        executor1.allowCoreThreadTimeOut(true);

        ArrayList<String> asList = new ArrayList<>();

        ReentrantLock lock = new ReentrantLock();
        lock.tryLock();
        asList.add("dd");

        // DefaultSingletonBeanRegistry



        return "";
    }


}
