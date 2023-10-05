



CREATE TABLE `tb_give_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `request_no` varchar(64) NOT NULL COMMENT '请求号',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `batch_no` varchar(64) NOT NULL COMMENT '批次号',
  `amount` decimal(15,2) NOT NULL COMMENT '分发金额',
  `merchant_no` varchar(64) NOT NULL COMMENT '商户编号',
  `out_acc_no` varchar(64) DEFAULT NULL COMMENT '分发账号',
  `in_acc_no` varchar(64) DEFAULT NULL COMMENT '目标账户',
  `user_id` varchar(64) DEFAULT NULL COMMENT '龙民ID',
  `account_type` tinyint(4) NOT NULL COMMENT '账户类型 10:内部 11:外部 12:管理 31:商户',
  `data_type` tinyint(4) DEFAULT NULL COMMENT '数据类型 1-toC 2-toB',
  `give_type` tinyint(4) NOT NULL COMMENT '分发方式 0-同步; 1-异步',
  `status` tinyint(4) NOT NULL COMMENT '状态 0:处理中 1:成功 2:失败',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注',
  `failure_msg` varchar(256) DEFAULT NULL COMMENT '失败原因',
  `activity_no` varchar(64) DEFAULT NULL COMMENT '活动编号',
  `prod_type` varchar(32) DEFAULT NULL COMMENT '业务类型编号',
  `trade_summary` varchar(256) DEFAULT NULL COMMENT '分发摘要',
  `exclusive_no` varchar(64) DEFAULT NULL COMMENT '专用编号',
  `channel` varchar(100) DEFAULT NULL COMMENT '渠道信息',
  `app_id` varchar(50) NOT NULL COMMENT 'app id',
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `source` varchar(32) DEFAULT NULL COMMENT '来源',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_appid_requestno_batchNo` (`app_id`,`request_no`,`batch_no`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_request_no` (`request_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=300000000 DEFAULT CHARSET=utf8mb4 COMMENT='积分分发订单表';


CREATE TABLE `tb_give_batch_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
  `app_id` varchar(50) NOT NULL COMMENT '业务系统ID',
  `batch_no` varchar(64) NOT NULL COMMENT '分发批次号',
  `activity_type` varchar(64) NOT NULL COMMENT '活动类型',
  `merchant_no` varchar(255) DEFAULT NULL COMMENT '商户编号',
  `serial_no` varchar(128) DEFAULT NULL COMMENT '费控单编号',
  `give_count` int(20) NOT NULL COMMENT '总分发人数',
  `receive_count` int(20) NOT NULL COMMENT '已接收人数',
  `result_type` tinyint(4) NOT NULL COMMENT '分发结果类型 0:整体成功 1:部分成功',
  `status` tinyint(4) NOT NULL COMMENT '信息状态 0:处理中 1:处理完成',
  `error_msg` varchar(255) DEFAULT NULL COMMENT '失败原因',
  `amount` decimal(15,2) DEFAULT NULL COMMENT '分发总数量',
  `channel` varchar(100) NOT NULL COMMENT '分发渠道',
  `activity_no` varchar(64) DEFAULT NULL COMMENT '活动编号',
  `out_acc_no` varchar(32) DEFAULT NULL COMMENT '转出账号',
  `source` varchar(32) DEFAULT NULL COMMENT '来源',
  `deal_count` int(20) DEFAULT NULL COMMENT '已处理条数',
  `asyn_flag` tinyint(4) DEFAULT NULL COMMENT '分发方式 0:同步 1:异步',
  `callback_url` varchar(300) DEFAULT NULL COMMENT '回调地址',
  `callback_identity` varchar(300) DEFAULT NULL COMMENT '回调方身份标志',
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
  `expire_time` varchar(32) DEFAULT NULL COMMENT '珑珠有效期',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_app_id_batch_no_source` (`app_id`,`batch_no`,`source`) USING BTREE COMMENT 'appId、批次号、来源',
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_batch_no` (`batch_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200000000 DEFAULT CHARSET=utf8mb4 COMMENT='积分分发信息表';


CREATE TABLE `tb_give_refund_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `app_id` varchar(50) DEFAULT NULL COMMENT '业务系统ID',
  `batch_no` varchar(64) DEFAULT NULL COMMENT '原发放批次号',
  `refund_no` varchar(64) NOT NULL COMMENT '原分发流水号',
  `request_no` varchar(64) NOT NULL COMMENT '本次请求号',
  `amount` decimal(15,2) NOT NULL COMMENT '请求撤回金额',
  `refunded_amount` decimal(15,1) DEFAULT NULL COMMENT '实际撤回金额',
  `status` tinyint(4) NOT NULL COMMENT '状态 0:处理中 1:成功 2:失败',
  `error_msg` varchar(256) DEFAULT NULL COMMENT '失败原因',
  `remark` varchar(256) DEFAULT NULL COMMENT '备注',
  `refund_type` tinyint(4) NOT NULL COMMENT '撤回类型 1：撤回并销毁 2：仅撤回到账户余额',
  `account_type` tinyint(4) DEFAULT NULL COMMENT '撤回账号类型 10-消费,11-外部,12-管理者',
  `handle_debit` tinyint(4) DEFAULT NULL COMMENT '是否允许欠款 0-否 1-是',
  `handle_settle` tinyint(4) DEFAULT NULL COMMENT '是否处理结算 0-否 1-是',
  `auto_refund` tinyint(4) DEFAULT NULL COMMENT '是否为自动撤回 0-否 1-是',
  `user_id` varchar(64) DEFAULT NULL COMMENT '用户id',
  `trans_out` varchar(50) DEFAULT NULL COMMENT '转出账户',
  `trans_in` varchar(50) DEFAULT NULL COMMENT '转入账户',
  `trade_summary` varchar(200) DEFAULT NULL COMMENT '账单摘要',
  `source` varchar(32) DEFAULT NULL COMMENT '来源',
  `create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(6) DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_refund_no_request_no` (`app_id`,`request_no`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE,
  KEY `idx_status` (`status`) USING BTREE,
  KEY `idx_refundno` (`refund_no`) USING BTREE,
  KEY `idx_refundamt` (`refunded_amount`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT= 200000000 DEFAULT CHARSET=utf8mb4 COMMENT='积分分发撤回表';


CREATE TABLE `tb_ctrans_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `source` varchar(32) NOT NULL COMMENT '来源',
    `fid` bigint(20) NOT NULL COMMENT '记录Id',
    `request_no` varchar(64) NOT NULL COMMENT '订单明细号',
    `user_id` varchar(64) DEFAULT NULL COMMENT 'userId',
    `account_type` tinyint(4) NOT NULL COMMENT '账户类型 10:内部 11:外部 12:管理 31:商户',
    `prod_type` varchar(64) DEFAULT NULL COMMENT '业务类型',
    `action_type` tinyint(4) NOT NULL COMMENT '操作类型',
    `amount` decimal(18,2) unsigned NOT NULL COMMENT '交易金额',
    `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '流水状态:0-处理中、1-成功、2-失败',
    `error_msg` varchar(255) DEFAULT NULL COMMENT '失败原因',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `create_time` datetime(6) NOT NULL COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL COMMENT '更新时间',
    `seq` bigint(20) NOT NULL DEFAULT '0' COMMENT '序号',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_fid_source_action_type` (`fid`,`source`,`action_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=200000000 DEFAULT CHARSET=utf8mb4 COMMENT='C端操作流水';


CREATE TABLE `tb_btrans_log` (
   `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '自增主键',
   `source` varchar(32) NOT NULL COMMENT '来源',
   `fid` bigint(20) NOT NULL COMMENT '记录Id',
   `request_no` varchar(64) NOT NULL COMMENT '订单明细号',
   `acc_no` varchar(32) NOT NULL COMMENT '出账账户',
   `action_type` tinyint(2) NOT NULL COMMENT '操作类型',
   `prod_type` varchar(64) DEFAULT NULL COMMENT '业务类型',
   `amount` decimal(15,1) unsigned NOT NULL COMMENT '交易金额',
   `status` tinyint(2) NOT NULL DEFAULT '1' COMMENT '状态: 0-处理中、1-成功、2-失败',
   `error_msg` varchar(255) DEFAULT NULL COMMENT '失败原因',
   `remark` varchar(255) DEFAULT NULL COMMENT '备注',
   `app_id` varchar(64) DEFAULT NULL COMMENT 'appId',
   `exclusive_no` varchar(64) DEFAULT NULL COMMENT '专用编号',
   `activity_type` varchar(64) DEFAULT NULL COMMENT '活动类型',
   `other_acc_no` varchar(64) DEFAULT NULL COMMENT '对手方账户',
   `seq` bigint(20) NOT NULL DEFAULT '0' COMMENT '序号',
   `create_time` datetime(6) NOT NULL COMMENT '创建时间',
   `update_time` datetime(6) NOT NULL COMMENT '更新时间',
   PRIMARY KEY (`id`) USING BTREE,
   UNIQUE KEY `uk_fid_source_acc_no_action_type_seq` (`fid`,`source`,`acc_no`,`action_type`,`seq`) USING BTREE,
   KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200000000 DEFAULT CHARSET=utf8mb4 COMMENT='B端操作流水';


