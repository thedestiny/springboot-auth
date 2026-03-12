package com.platform.sandboxsatoken.config;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.platform.sandboxsatoken.entity.User;
import com.platform.sandboxsatoken.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限数据源
 * Sa-Token 调用 @SaCheckRole / @SaCheckPermission 注解时，会从这里获取当前用户的角色列表和权限列表。
 * 必须实现此接口，否则注解鉴权不生效。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    public StpInterfaceImpl(UserService userService) {
        this.userService = userService;
    }

    /**
     * 返回当前账号拥有的权限码集合
     * 权限码格式：模块:操作，如 user:list、order:add
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        User user = userService.getById(Long.parseLong(loginId.toString()));
        if (user == null) {
            return new ArrayList<>();
        }
        return PermissionConstants.getPermissions(user.getRole());
    }

    /**
     * 返回当前账号拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 优先从 Session 缓存取，避免每次都查库
        Object roleInSession = StpUtil.getSession().get("role");
        if (roleInSession != null) {
            return List.of(roleInSession.toString());
        }
        User user = userService.getById(Long.parseLong(loginId.toString()));
        if (user == null) {
            return new ArrayList<>();
        }
        return List.of(user.getRole());
    }
}
