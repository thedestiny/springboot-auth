package com.platform.sandboxsatoken.service;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.platform.sandboxsatoken.dto.LoginRequest;
import com.platform.sandboxsatoken.dto.RegisterRequest;
import com.platform.sandboxsatoken.entity.User;

public interface UserService {

    /** 注册新用户，返回用户信息 */
    User register(RegisterRequest req);

    /** 登录，返回 Sa-Token 信息（含 token） */
    SaTokenInfo login(LoginRequest req);

    /** 根据 id 查询用户 */
    User getById(Long id);
}
