package com.platform.productserver.service;

import com.platform.productserver.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface UserService extends IService<User> {

    User selectByUserId(String userId);




}
