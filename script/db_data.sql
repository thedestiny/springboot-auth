

CREATE TABLE `tb_user` (
   `id`  bigint NOT NULL AUTO_INCREMENT,
   `typ` varchar(255) DEFAULT NULL,
   `username` varchar(255) NOT NULL COMMENT '用户名称',
   `password` varchar(255) NOT NULL COMMENT '用户密码',
   `enabled`  tinyint(2) DEFAULT '1' COMMENT '用户状态',
   PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1783 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

DROP TABLE IF EXISTS `oauth_access_token`;
CREATE TABLE `oauth_access_token`  (
  `token_id` varchar(255)  NULL DEFAULT NULL,
  `token` blob NULL,
  `authentication_id` varchar(255)  NULL DEFAULT NULL,
  `user_name` varchar(255)  NULL DEFAULT NULL,
  `client_id` varchar(255)  NULL DEFAULT NULL,
  `authentication` blob NULL,
  `refresh_token` varchar(255)  NULL DEFAULT NULL
) ENGINE = InnoDB ;


DROP TABLE IF EXISTS `oauth_approvals`;
CREATE TABLE `oauth_approvals`  (
    `userId` varchar(128)  NULL DEFAULT NULL,
    `clientId` varchar(128)  NULL DEFAULT NULL,
    `scope` varchar(128)  NULL DEFAULT NULL,
    `status` varchar(10)  NULL DEFAULT NULL,
    `expiresAt` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
    `lastModifiedAt` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE = InnoDB ;


DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details`  (
     `client_id` varchar(48)  NOT NULL COMMENT 'client_id',
     `resource_ids` varchar(256)  NULL DEFAULT NULL COMMENT 'resource_id',
     `client_secret` varchar(256)  NULL DEFAULT NULL COMMENT 'client_secret',
     `scope` varchar(256)  NULL DEFAULT NULL COMMENT '授权范围',
     `authorized_grant_types` varchar(256)  NULL DEFAULT NULL COMMENT '授权模式',
     `web_server_redirect_uri` varchar(256)  NULL DEFAULT NULL COMMENT '跳转地址',
     `authorities` varchar(256)  NULL DEFAULT NULL COMMENT '角色',
     `access_token_validity` int(11) NULL DEFAULT NULL COMMENT '获取token有效期',
     `refresh_token_validity` int(11) NULL DEFAULT NULL COMMENT '刷新token有效期',
     `additional_information` varchar(4096)  NULL DEFAULT NULL COMMENT '额外信息',
     `autoapprove` varchar(256)  NULL DEFAULT NULL COMMENT 'autoapprove',
     PRIMARY KEY (`client_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'oauth table ' ROW_FORMAT = Dynamic;

DROP TABLE IF EXISTS `oauth_client_token`;
CREATE TABLE `oauth_client_token`  (
     `token_id` varchar(255)  NULL DEFAULT NULL,
     `token` blob NULL,
     `authentication_id` varchar(255)  NULL DEFAULT NULL,
     `user_name` varchar(255)  NULL DEFAULT NULL,
     `client_id` varchar(255)  NULL DEFAULT NULL
) ENGINE = InnoDB ;


DROP TABLE IF EXISTS `oauth_code`;
CREATE TABLE `oauth_code`  (
    `code` varchar(255)  NULL DEFAULT NULL,
    `authentication` blob NULL
) ENGINE = InnoDB ;


DROP TABLE IF EXISTS `oauth_refresh_token`;
CREATE TABLE `oauth_refresh_token`  (
     `token_id` varchar(255)  NULL DEFAULT NULL,
     `token` blob NULL,
     `authentication` blob NULL
) ENGINE = InnoDB ;

-- client 配置信息
INSERT INTO `oauth_client_details` VALUES ('clientapp', 'micro-oauth', '112233', 'read_userinfo,read_contacts', 'authorization_code,refresh_token', 'http://127.0.0.1:9501/callback', 'ROLE_USER', 360000, 360000, NULL, 'false');
INSERT INTO `oauth_client_details` VALUES ('client_001', 'client_resource', '112233', 'all', 'authorization_code,refresh_token', 'http://127.0.0.1:9501/callback', 'ROLE_USER', 360000, 360000, NULL, 'false');

