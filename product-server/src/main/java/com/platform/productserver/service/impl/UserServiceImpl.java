package com.platform.productserver.service.impl;

import com.platform.productserver.entity.User;
import com.platform.productserver.mapper.UserMapper;
import com.platform.productserver.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
