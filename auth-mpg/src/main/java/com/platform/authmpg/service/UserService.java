package com.platform.authmpg.service;

import com.platform.authmpg.dto.UserDto;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-07-23 12:34 PM
 */
public interface UserService {


    UserDto selectByUserId(String userId);
}
