package com.platform.sandboxsatoken.config;

import java.util.List;
import java.util.Map;

/**
 * 权限常量定义
 * 按角色映射其拥有的权限码集合
 */
public class PermissionConstants {

    // 权限码：模块:操作
    public static final String USER_LIST   = "user:list";
    public static final String USER_ADD    = "user:add";
    public static final String USER_EDIT   = "user:edit";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_INFO   = "user:info";

    public static final String ORDER_LIST  = "order:list";
    public static final String ORDER_ADD   = "order:add";

    /** 角色 -> 权限码列表 */
    private static final Map<String, List<String>> ROLE_PERMISSIONS = Map.of(
            "admin", List.of(USER_LIST, USER_ADD, USER_EDIT, USER_DELETE, USER_INFO,
                             ORDER_LIST, ORDER_ADD),
            "user",  List.of(USER_INFO, ORDER_LIST)
    );

    public static List<String> getPermissions(String role) {
        return ROLE_PERMISSIONS.getOrDefault(role, List.of());
    }
}
