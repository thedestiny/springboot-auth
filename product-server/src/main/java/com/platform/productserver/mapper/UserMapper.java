package com.platform.productserver.mapper;

import com.platform.productserver.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据 userid 查询用户信息
     */
    User selectByUserId(String userId);

}
