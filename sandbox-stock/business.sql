CREATE TABLE `tb_reservation` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
     `order_id` bigint DEFAULT '0' COMMENT '订单号',
     `item_id` bigint DEFAULT '0' COMMENT '项目id',
     `masseur_id` bigint DEFAULT '0' COMMENT '家政服务人员id',
     `shop_id` bigint DEFAULT '0' COMMENT '店铺id',
     `node_time` varchar(20) COMMENT '预约日期',
     `user_id` bigint DEFAULT '0' COMMENT '用户id',
     `start_time` datetime DEFAULT NULL COMMENT '预约开始时间',
     `end_time` datetime DEFAULT NULL COMMENT '预约结束时间',
     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
     `seq` bigint DEFAULT '0' COMMENT 'seq'
      PRIMARY KEY (`id`) USING BTREE,
     KEY `idx_masseur_id` (`order_id`,`masseur_id`,`shop_id`,`user_id`,`item_id`) USING BTREE,
     KEY `idx_res_id` (`res_id`) USING BTREE,
     KEY `idx_node_time` (`node_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100000 COMMENT='用户预约表';

CREATE TABLE `tb_reservation_detail` (
      `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
      `order_id` bigint(20) DEFAULT '0' COMMENT '订单号',
      `masseur_id`  bigint(20) DEFAULT '0' COMMENT '家政服务人员id',
      `reservation_date`  varchar(20) DEFAULT '0' COMMENT '预约日期',
      `reservation_time`  varchar(20) DEFAULT '0' COMMENT '预约时间',
      `seq`  bigint(20) DEFAULT '0' COMMENT '删除标志 0代表存在 其他为不存在',
      `remark`  varchar(20) DEFAULT '' COMMENT '备注',
      `create_time` datetime DEFAULT NULL COMMENT '创建时间',
      PRIMARY KEY (`id`),
      UNIQUE KEY `uk_masseur_id_reservation_time_seq` (`masseur_id`, `reservation_date`,  `reservation_time`, `seq`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 COMMENT='家政人员预约明细表';

CREATE TABLE `tb_item_info` (
     `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '项目id',
     `item_name` varchar(30) NOT NULL COMMENT '项目名称',
     `price` decimal(10,2) default '0' COMMENT '项目价格',
     `cost_time` int default '0' COMMENT '项目时长,单位为分钟',
     `brief` text NULL COMMENT '简介',
     `seq`  bigint(20) DEFAULT '0' COMMENT '删除标志 0代表存在 其他为不存在',
     `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
     `create_time` datetime DEFAULT NULL COMMENT '创建时间',
     `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
     `remark` varchar(500) DEFAULT NULL COMMENT '备注',
     PRIMARY KEY (`id`),
     UNIQUE KEY `uk_item_name_del_flag` (`item_name`, `seq`)
) ENGINE=InnoDB AUTO_INCREMENT=100000 DEFAULT CHARSET=utf8mb4 COMMENT='项目名称';
