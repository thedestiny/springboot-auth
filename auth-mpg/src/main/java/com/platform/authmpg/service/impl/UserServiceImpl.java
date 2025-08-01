package com.platform.authmpg.service.impl;

import com.platform.authmpg.dto.UserDto;
import com.platform.authmpg.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * read/write through 机制 项目启动后系统初始化将数据写入缓存
 * cache aside 旁路缓存模式,
 * 1 读流程 查询数据时先查询缓存,如果缓存不存在,则查询数据库,并将数据写入缓存
 * 2 写流程 先更新数据库，再删除或者更新缓存旧数据
 * @Description
 * @Author liangkaiyang
 * @Date 2025-07-23 12:35 PM
 */

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserDto selectByUserId(String userId) {
        return new UserDto();
    }

    @Transactional
    public void innerMethod() {
         // 正常事务逻辑
    }

    public void outerMethod() {
        // 直接调用 innerMethod 会跳过代理，导致事务失效
        // 正确方式：通过代理对象调用
        ((UserServiceImpl) AopContext.currentProxy()).innerMethod();

        // 提供静态方法快速识别代理类型，便于动态处理不同代理逻辑
        // 例如：判断是否代理对象、获取目标对象等
        AopUtils.isAopProxy(this);
        AopUtils.isCglibProxy(this);
        AopUtils.isJdkDynamicProxy(this);

        // 获取目标对象
        Object target = AopUtils.getTargetClass(this);
        if (target instanceof UserServiceImpl) {
            UserServiceImpl targetObject = (UserServiceImpl) target;
            // 可以调用目标对象的方法
            targetObject.innerMethod();
        }

        UserDto dto = new UserDto();
        Field id = ReflectionUtils.findField(UserDto.class, "id");
        // 设置属性被访问
        ReflectionUtils.makeAccessible(id);
        // 获取属性的值
        Object value = ReflectionUtils.getField(id, dto);

        // Method method = ReflectionUtils.findMethod(UserDto.class,"privateMethod", String.class);
        Method method = ReflectionUtils.findMethod(UserDto.class,"setName", String.class);
        ReflectionUtils.invokeMethod(method, dto,"参数");


    }
}
