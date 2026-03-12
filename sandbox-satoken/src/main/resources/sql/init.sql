-- 创建数据库
CREATE DATABASE IF NOT EXISTS satoken_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE satoken_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username`    VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`    VARCHAR(100) NOT NULL COMMENT '密码（MD5）',
    `nickname`    VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `role`        VARCHAR(50)  NOT NULL DEFAULT 'user' COMMENT '角色：admin/user',
    `status`      INT          NOT NULL DEFAULT 1 COMMENT '状态：1=正常，0=禁用',
    `create_time` DATETIME     DEFAULT NULL COMMENT '创建时间',
    `deleted`     INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- 初始管理员账号（密码：admin123，MD5：0192023a7bbd73250516f069df18b500）
-- 角色 admin 拥有权限：user:list, user:add, user:edit, user:delete, user:info, order:list, order:add
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`, `create_time`)
VALUES ('admin', '0192023a7bbd73250516f069df18b500', '管理员', 'admin', 1, NOW());

-- 初始普通用户（密码：user123，MD5：a1a18f7e04db1b84b4e8c048e9b7ea73）
-- 角色 user 拥有权限：user:info, order:list
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`, `create_time`)
VALUES ('user1', 'a1a18f7e04db1b84b4e8c048e9b7ea73', '普通用户', 'user', 1, NOW());

/*
角色与权限对应关系（见 PermissionConstants.java）：
  admin -> user:list, user:add, user:edit, user:delete, user:info, order:list, order:add
  user  -> user:info, order:list

测试接口权限验证：
  GET /test/user-list  需要 user:list 权限 -> admin 可访问，user1 不可访问
  GET /test/order-add  需要 order:add 权限 -> admin 可访问，user1 不可访问
  GET /test/admin      需要 admin 角色     -> admin 可访问，user1 不可访问
  GET /test/private    仅需登录           -> 两者均可访问
  GET /test/my-auth    查看自己的角色和权限列表
*/
