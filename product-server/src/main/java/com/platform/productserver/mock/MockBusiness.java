package com.platform.productserver.mock;

import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.config.RedisUtils;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.productserver.dto.UserDto;
import com.platform.productserver.entity.User;
import com.platform.productserver.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 单元测试综合运用
 *
 * @Description
 * @Date 2023-09-06 5:05 PM
 */

@Slf4j
@Service
public class MockBusiness {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TransactionTemplate transaction;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RestTemplate restTemplate;

    // 设置 属性
    @Value("${app.name:''}")
    private String name;

    public UserDto mockTest(UserDto dto) throws InterruptedException {

        log.info("name is {}", name);
        UserDto result = new UserDto();
        // 1 单测点 mock 静态方法
        String id = IdGenUtils.id();
        // 2 单测点 mock 分布式锁
        RLock lock = redisUtils.getLock("order" + id);
        if (!lock.tryLock(1, TimeUnit.MINUTES)) {
            return result;
        }
        // 3 单测点 mock userMapper
        User query = userMapper.selectByUserId(String.valueOf(dto.getId()));
        if (query == null) {
            log.info("info not exist ! {}", JSONObject.toJSONString(dto));
            return null;
        }
        // 设置请求头和报文
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HashMap<String, Object> map = new HashMap<>();
        map.put("username", "zhangsan");
        //用HttpEntity封装整个请求报文
        HttpEntity<HashMap<String, Object>> request = new HttpEntity<>(map, headers);
        JSONObject response = restTemplate.postForObject("http://baidu.com", request, JSONObject.class);
        log.info("response is {}", response);

        // 4 单测点 mock redisTemplate opsForValue().increment
        Long increment = redisTemplate.opsForValue().increment(id);
        if (increment != null && increment > 0) {
            // 5 单测点  transaction.execute
            Object obj = transaction.execute(status -> {
                try {
                    User user = userMapper.selectByUserId(String.valueOf(dto.getId()));
                    if (user != null) {
                        user.setSeq(increment);
                        userMapper.updateById(user);
                    }
                    return true;
                } catch (Exception e) {
                    log.error("error info {} ", e.getMessage(), e);
                    status.setRollbackOnly();
                    throw e;
                }
            });

            if (obj instanceof Exception) {
                throw new RuntimeException("保存数据失败!");
            }
            boolean flag = (Boolean) obj;
            return flag ? result : null;

        }

        return null;
    }


}
