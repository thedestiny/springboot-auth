package com.platform.controller;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
public class SseController {


    private final Map<String, SseEmitter> emitters = new
            ConcurrentHashMap<>();

    /**
     * 创建 sse 控制器
     */
    @GetMapping("/api/data/stream")
    public SseEmitter streamData(String uid) {
        SseEmitter emitter = new SseEmitter(0L);

        emitter.onCompletion(() -> {
            log.info("completion {}", uid);
            emitters.remove(uid);
        });

        emitter.onTimeout(() -> {
            log.info("timout {}", uid);
        });

        emitter.onError(the -> {
            log.info("{} error {}", uid, the.getMessage(), the);
            try {
                emitter.send(SseEmitter.event().id(uid).name("异常").data("发送异常").reconnectTime(3000));
                emitters.put(uid, emitter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        try {
            emitter.send(SseEmitter.event().reconnectTime(5000));
        } catch (IOException e) {
            e.printStackTrace();
        }
        emitters.put(uid, emitter);


//        emitter.onCompletion(() -> emitters.remove(emitter));
//        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    public boolean sendMessage(String uid, String messageId, String message) {
        if (StrUtil.isEmpty(message)) {
            log.info("[{}]参数异常，msg为空", uid);
            return false;
        }
        SseEmitter sseEmitter = emitters.get(uid);
        if (sseEmitter == null) {
            log.info("[{}]sse连接不存在", uid);
            return false;
        }
        try {
            sseEmitter.send(SseEmitter.event().id(messageId).reconnectTime(60000).data(message));
            log.info("用户{},消息ID：{}，推送成功：{}", uid, messageId, message);
            return true;
        } catch (IOException e) {
            emitters.remove(uid);
            log.info("用户{},消息ID：{}，消息推送失败：{}", uid, messageId, message);
            sseEmitter.complete();
            return false;
        }
    }

    public void closeSse(String uid) {
        if (emitters.containsKey(uid)) {
            SseEmitter sseEmitter = emitters.get(uid);
            sseEmitter.complete();
            emitters.remove(uid);
        } else {
            log.info("用户{}连接已关闭", uid);
        }
    }


    /**
     * 定时任务发送数据
     */
    @Scheduled(fixedRate = 5000) // 每5秒发送一次数据
    public void sendData() {
        emitters.forEach((ke,em) -> {
            try {
                em.send(SseEmitter.event().name("message").data("Hello from server!")); // 发送事件和数据
            } catch (IOException e) {
                // 处理异常，例如移除失效的emitter等。
                System.err.println("Error sending data: " + e.getMessage());
            }
        });
    }

}
