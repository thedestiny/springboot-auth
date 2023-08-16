

CREATE TABLE `tb_stock_info` (
     `id` varchar(10) NOT NULL COMMENT '代码',
     `name` varchar(20) DEFAULT NULL COMMENT '股票名称',
     `amount` decimal(11,3) DEFAULT NULL COMMENT '成交额(亿元)',
     `amplitude` decimal(11,3) DEFAULT NULL COMMENT '振幅,百分比',
     `chg` decimal(11,3) DEFAULT NULL COMMENT '涨跌(元)',
     `current` decimal(11,3) DEFAULT NULL COMMENT '当前价格',
     `current_year_percent` decimal(11,3) DEFAULT NULL COMMENT '当年涨跌(%)',
     `dividend_yield` decimal(11,3) DEFAULT NULL COMMENT '股息率ttm(%)',
     `eps` decimal(11,3) DEFAULT NULL COMMENT '每股收益(元)',
     `float_market_capital` decimal(11,3) DEFAULT NULL COMMENT '流通市值(亿元)',
     `float_shares` decimal(11,3) DEFAULT NULL COMMENT '流通股(亿)',
     `total_shares` decimal(11,3) DEFAULT NULL COMMENT '总股本(亿)',
     `pb` decimal(11,3) DEFAULT NULL COMMENT '市净率',
     `pb_ttm` decimal(11,3) DEFAULT NULL COMMENT '市净率(ttm)',
     `pe_ttm` decimal(11,3) DEFAULT NULL COMMENT '市盈率(ttm)',
     `percent` decimal(11,3) DEFAULT NULL COMMENT '当天涨跌幅(%)',
     `pe` decimal(11,3) DEFAULT NULL COMMENT '市盈率(动态)',
     `net_value` decimal(11,3) DEFAULT NULL COMMENT '每股净资产',
     `high_year` decimal(11,3) DEFAULT NULL COMMENT '52周最高',
     `low_year` decimal(11,3) DEFAULT NULL COMMENT '52周最低',
     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP  COMMENT '更新时间',
     `focus` int(11) DEFAULT NULL COMMENT '关注度',
     `market_capital` decimal(11,3) DEFAULT NULL COMMENT '总市值(亿元)',
     `turnover_rate` decimal(11,3) DEFAULT NULL COMMENT '换手率(%)',
     `choice` int(11) DEFAULT '0' COMMENT '选择结果 1即选中 0 未选中',
     PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = 'stock信息表';




CREATE TABLE `tb_pkg_in_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `fid` bigint(20) NOT NULL COMMENT '红包发放记录ID',
  `source` varchar(32) NOT NULL COMMENT '来源',
  `request_no` varchar(50)  NOT NULL COMMENT '请求号',
  `order_no` varchar(50)  DEFAULT NULL COMMENT '红包订单号',
  `user_id` varchar(20) NOT NULL COMMENT '领红包用户ID',
  `amount` decimal(15,2) NOT NULL COMMENT '领取金额',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '订单状态: 0- 处理中、1-成功、 2-失败',
  `error_msg` varchar(255)  DEFAULT NULL COMMENT '异常原因',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `prod_type` varchar(32)  NOT NULL COMMENT '产品类型：个人红包-100 群红包-101',
  `action_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '操作：0-领取  1-退回',
  `remark` varchar(255)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `un_reqno_source` (`request_no`,`source`,`user_id`) USING BTREE
) ENGINE=InnoDB COMMENT='红包领取记录表';


CREATE TABLE `tb_pkg_out_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `source` varchar(32)  NOT NULL COMMENT '来源',
  `app_id` varchar(32)  NOT NULL COMMENT '应用ID',
  `order_no` varchar(50)  DEFAULT NULL COMMENT '红包订单号',
  `prod_type` varchar(32)  NOT NULL COMMENT '产品类型：个人红包-100 群红包-101',
  `user_id` varchar(20) NOT NULL COMMENT '发红包用户ID',
  `amount` decimal(15,2) NOT NULL COMMENT '红包金额',
  `receive_amount` decimal(15,2) NOT NULL DEFAULT '0.00' COMMENT '已经领取金额',
  `refund_amount` decimal(15,1) NOT NULL DEFAULT '0.00' COMMENT '红包退回金额',
  `status` tinyint(4) NOT NULL COMMENT '订单状态：0-处理中、1-成功、2-失败',
  `error_msg` varchar(255)  DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  `expire_time` datetime(6) NOT NULL COMMENT '红包到期时间',
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  `flag` tinyint(4) NOT NULL DEFAULT '0' COMMENT '完成标识 0-未完成  1-已完成',
  `remark` varchar(255)  DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_source_package_no` (`order_no`,`source`) USING BTREE,
  KEY `idx_expire_time` (`expire_time`) USING BTREE
) ENGINE=InnoDB  COMMENT='红包订单表';


