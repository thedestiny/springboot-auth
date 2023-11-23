

CREATE TABLE `tb_student` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username` varchar(20) DEFAULT NULL COMMENT '用户id',
  `weight` decimal(18,2) DEFAULT NULL COMMENT '体重',
  `age` int DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `id_card` varchar(20) DEFAULT NULL COMMENT '身份证号',
  `address` varchar(200) DEFAULT NULL COMMENT '地址',
  `birthday` datetime NOT NULL COMMENT '出生年月',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=300000 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='学生表';
