package com.platform.sandboxsatoken.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.sandboxsatoken.dto.LoginRequest;
import com.platform.sandboxsatoken.dto.RegisterRequest;
import com.platform.sandboxsatoken.entity.User;
import com.platform.sandboxsatoken.mapper.UserMapper;
import com.platform.sandboxsatoken.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User register(RegisterRequest req) {
        // 校验用户名是否已存在
        long count = count(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));
        if (count > 0) {
            throw new RuntimeException("用户名已存在");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        // MD5 加密密码（实际项目建议使用 BCrypt）
        user.setPassword(md5(req.getPassword()));
        user.setNickname(req.getNickname() != null ? req.getNickname() : req.getUsername());
        user.setRole("user");
        user.setStatus(1);
        save(user);
        user.setPassword(null); // 不返回密码
        return user;
    }

    @Override
    public SaTokenInfo login(LoginRequest req) {
        User user = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername())
                .eq(User::getStatus, 1));
        if (user == null) {
            throw new RuntimeException("用户不存在或已被禁用");
        }
        if (!md5(req.getPassword()).equals(user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        // Sa-Token 登录，存储用户 id
        StpUtil.login(user.getId());
        // 将角色信息写入 session，供后续权限校验使用
        StpUtil.getSession().set("role", user.getRole());
        StpUtil.getSession().set("username", user.getUsername());
        return StpUtil.getTokenInfo();
    }

    @Override
    public User getById(Long id) {
        User user = super.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    private String md5(String raw) {
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }




}
