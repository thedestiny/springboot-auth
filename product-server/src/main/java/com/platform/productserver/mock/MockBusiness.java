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
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 单元测试综合运用
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

    @Value("${app.name:''}")
    private String name;

    public UserDto mockTest(UserDto dto) throws InterruptedException {

        UserDto result = new UserDto();
        // mock 1
        String id = IdGenUtils.id();
        // mock 2
        RLock lock = redisUtils.getLock("order" + id);
        if (!lock.tryLock(1, TimeUnit.MINUTES)) {
            return result;
        }
        // mock 3
        User query = userMapper.selectByUserId(String.valueOf(dto.getId()));
        if (query == null) {
            log.info("info not exist ! {}", JSONObject.toJSONString(dto));
            return null;
        }
        // mock 4  获取自增值
        Long increment = redisTemplate.opsForValue().increment(id);
        if (increment != null && increment > 0) {

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
