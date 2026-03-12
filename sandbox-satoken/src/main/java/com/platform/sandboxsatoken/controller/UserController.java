package com.platform.sandboxsatoken.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.platform.sandboxsatoken.common.Result;
import com.platform.sandboxsatoken.dto.LoginRequest;
import com.platform.sandboxsatoken.dto.RegisterRequest;
import com.platform.sandboxsatoken.entity.User;
import com.platform.sandboxsatoken.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 注册 */
    @PostMapping("/register")
    public Result<User> register(@RequestBody RegisterRequest req) {
        return Result.ok(userService.register(req));
    }

    /** 登录，返回 token */
    @PostMapping("/login")
    public Result<SaTokenInfo> login(@RequestBody LoginRequest req) {
        return Result.ok(userService.login(req));
    }

    /** 注销登录 */
    @PostMapping("/logout")
    public Result<Void> logout() {
        StpUtil.logout();
        return Result.ok();
    }

    /** 获取当前登录用户信息（需登录） */
    @GetMapping("/info")
    public Result<User> info() {
        long loginId = StpUtil.getLoginIdAsLong();
        return Result.ok(userService.getById(loginId));
    }

    /** 检查是否已登录 */
    @GetMapping("/isLogin")
    public Result<Boolean> isLogin() {
        return Result.ok(StpUtil.isLogin());
    }
}
