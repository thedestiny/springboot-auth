package com.platform.authcommon.task;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时任务 异步消息
 * <p>
 * 批量推送消息，通过redis zset 数据结构实现，
 * 每个用户的消息都有一个时间戳，当用户订阅消息时，将用户id和当前时间戳添加到zset中，
 * 当用户取消订阅时，将用户id从zset中删除，
 * 定时任务，每分钟从zset中取出所有用户id，
 * 并根据时间戳判断是否需要推送消息，如果需要则推送消息，
 * 并将用户id从zset中删除，
 * <p>
 * 对用户id 取模放入不同的队列，即 MSG_KEY 中
 *
 * @Description
 * @Author liangkaiyang
 * @Date 2025-09-18 5:53 PM
 */

@Slf4j
@Component
public class PushMsgTask {


    @Autowired
    private RedisTemplate redisTemplate;

    private static final String MSG_KEY = "push-msg-key";

    /**
     * 用户订阅消息
     */
    public void subscribeMsg(String userId) {

        userId = StrUtil.isEmpty(userId) ? IdWorker.getIdStr() : userId;
        int i = userId.hashCode();
        // 取模，将用户id放入不同的队列
        String key = MSG_KEY + "-" + String.format("%02d", (i % 10));
        // 添加订阅
        redisTemplate.opsForZSet().add(key, userId, System.currentTimeMillis());
    }


    /**
     * 推送消息,高频定时任务执行，分布式
     */
    @Scheduled(fixedRate = 1000 * 2)
    public void pushMsg() {
        log.info("pushMsg");
        // 当前机器的id
        String key = MSG_KEY + "-" + String.format("%02d", 2);

        // 指定范围数据
        Set range = redisTemplate.opsForZSet().range(key, 0, -1);

        // 数据的大小
        Long size = redisTemplate.opsForZSet().size(MSG_KEY);
        log.info("数据的大小:{}", size);

        // 处理完推送之后删除元素
        List<String> rangeList = new ArrayList<>();
        rangeList.add("12");
        rangeList.add("23");
        rangeList.add("34");
        redisTemplate.opsForZSet().remove(MSG_KEY, rangeList);


    }


    public static void main(String[] args) {


        String format = String.format("%02d", 2);
        System.out.println(format);

    }

}
