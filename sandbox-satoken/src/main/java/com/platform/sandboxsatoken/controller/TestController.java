package com.platform.sandboxsatoken.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.stp.StpUtil;
import com.platform.sandboxsatoken.common.Result;
import com.platform.sandboxsatoken.config.PermissionConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    /** 公开接口，无需登录 */
    @GetMapping("/public")
    public Result<String> publicApi() {
        return Result.ok("这是公开接口，无需登录");
    }

    /** 需要登录才能访问（注解方式） */
    @SaCheckLogin
    @GetMapping("/private")
    public Result<String> privateApi() {
        String username = (String) StpUtil.getSession().get("username");
        return Result.ok("你好，" + username + "！这是需要登录的接口");
    }

    /** 需要 admin 角色才能访问（@SaCheckRole） */
    @SaCheckRole("admin")
    @GetMapping("/admin")
    public Result<String> adminApi() {
        return Result.ok("你好，管理员！这是需要 admin 角色的接口");
    }

    /** 需要 user:list 权限才能访问（@SaCheckPermission） */
    @SaCheckPermission(PermissionConstants.USER_LIST)
    @GetMapping("/user-list")
    public Result<String> userListApi() {
        return Result.ok("你有 user:list 权限，可查看用户列表");
    }

    /** 需要 order:add 权限才能访问 */
    @SaCheckPermission(PermissionConstants.ORDER_ADD)
    @GetMapping("/order-add")
    public Result<String> orderAddApi() {
        return Result.ok("你有 order:add 权限，可创建订单");
    }

    /** 查看当前用户的角色和权限列表 */
    @SaCheckLogin
    @GetMapping("/my-auth")
    public Result<Map<String, Object>> myAuth() {
        long loginId = StpUtil.getLoginIdAsLong();
        return Result.ok(Map.of(
                "loginId", loginId,
                "roles", StpUtil.getRoleList(),
                "permissions", StpUtil.getPermissionList()
        ));
    }
}
