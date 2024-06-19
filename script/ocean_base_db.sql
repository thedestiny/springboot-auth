

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
     `issue` varchar(20) DEFAULT NULL COMMENT '上市时间',
     PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = 'stock信息表';

CREATE TABLE `tb_fund_info` (
      `code` varchar(20) NOT NULL COMMENT 'code',
      `name` varchar(100) NOT NULL COMMENT '基金名称',
      `week` decimal(18,2) NULL COMMENT '近一周涨幅',
      `month` decimal(18,2) NULL COMMENT '近一月涨幅',
      `month3` decimal(18,2) NULL COMMENT '近三月涨幅',
      `half` decimal(18,2) NULL COMMENT '近6月涨幅',
      `year` decimal(18,2) NULL COMMENT '近一年涨幅',
      `since` decimal(18,2) NULL COMMENT '今年来涨幅',
      `type` varchar(32)  NOT NULL DEFAULT '' COMMENT '基金大类',
      `fund_type` varchar(32)  NOT NULL DEFAULT '' COMMENT '基金类型',
      `manager` varchar(80)  NOT NULL DEFAULT '' COMMENT '基金经理',
      `company` varchar(80)  NOT NULL DEFAULT '' COMMENT '基金公司',
      `issue` varchar(32)  NOT NULL DEFAULT '' COMMENT '发行日期',
      `baseline` varchar(150)  NOT NULL DEFAULT '' COMMENT '业绩比较基准',
      `tracking` varchar(100)  NOT NULL DEFAULT '' COMMENT '跟踪标的',
      `fund_size` varchar(100)  NOT NULL DEFAULT '' COMMENT '基金规模',
      `share_size` varchar(100)  NOT NULL DEFAULT '' COMMENT '基金份额',
      `update_date` varchar(20) NOT NULL DEFAULT '0' COMMENT '数据更新日期',
      `fee` varchar(32)  NOT NULL DEFAULT '' COMMENT '手续费',
      `buy_fee` varchar(150)  NOT NULL DEFAULT '' COMMENT '申购手续费',
      `sell_fee` varchar(150)  NOT NULL DEFAULT '' COMMENT '赎回手续费',
      PRIMARY KEY (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='场外基金信息表';

CREATE TABLE `tb_etf_info` (
      `code` varchar(20) NOT NULL COMMENT 'code',
      `name` varchar(100) NOT NULL COMMENT '基金名称',
      `brief` varchar(100) NOT NULL COMMENT '基金简称',
      `price` decimal(18,4) NULL COMMENT '价格',
      `rate` decimal(18,4) NULL COMMENT '涨跌幅%',
      `week` decimal(18,2) NULL COMMENT '近一周涨幅',
      `month` decimal(18,2) NULL COMMENT '近一月涨幅',
      `month3` decimal(18,2) NULL COMMENT '近三月涨幅',
      `half` decimal(18,2) NULL COMMENT '近6月涨幅',
      `year` decimal(18,2) NULL COMMENT '今年涨幅',
      `year1` decimal(18,2) NULL COMMENT '近一年涨幅',
      `year2` decimal(18,2) NULL COMMENT '近一年涨幅',
      `year3` decimal(18,2) NULL COMMENT '近一年涨幅',
      `since` decimal(18,2) NULL COMMENT '成立以来涨幅',
      `fund_type` varchar(32)  NOT NULL DEFAULT '' COMMENT '基金类型',
      `manager` varchar(80)  NOT NULL DEFAULT '' COMMENT '基金经理',
      `company` varchar(80)  NOT NULL DEFAULT '' COMMENT '基金公司',
      `issue` varchar(32)  NOT NULL DEFAULT '' COMMENT '发行日期',
      `baseline` varchar(100)  NOT NULL DEFAULT '' COMMENT '业绩比较基准',
      `tracking` varchar(100)  NOT NULL DEFAULT '' COMMENT '跟踪标的',
      `fund_size` varchar(100)  NOT NULL DEFAULT '' COMMENT '基金规模',
      `share_size` varchar(100)  NOT NULL DEFAULT '' COMMENT '基金份额',
      `update_date` varchar(20) NOT NULL DEFAULT '0' COMMENT '数据更新日期',
      `market` varchar(32)   NULL DEFAULT '' COMMENT '市场',
      `detail` varchar(32)   NULL DEFAULT '' COMMENT '详情',
      PRIMARY KEY (`code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='ETF场内基金基本信息表';



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
  `prod_type` varchar(32)  NOT NULL COMMENT '业务类型：个人红包-100 群红包-101',
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


CREATE TABLE `tb_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(20) DEFAULT NULL COMMENT '用户id',
  `username` varchar(20) DEFAULT NULL COMMENT '用户名',
  `email` varchar(20) DEFAULT NULL COMMENT '邮箱',
  `cellphone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '用户状态：0-禁用 1-正常',
  `seq` bigint NOT NULL DEFAULT '0' COMMENT 'seq 序列号',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_id` (`user_id`, `seq`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


CREATE TABLE `tb_account` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(20) DEFAULT NULL COMMENT '用户id',
  `acc_no` varchar(20) DEFAULT NULL COMMENT '账户编号',
  `account_type` tinyint NOT NULL DEFAULT '1' COMMENT '账户类型：10-内部、12-外部、13-管理',
  `balance` decimal(18,2) DEFAULT NULL COMMENT '用户余额',
  `status` varchar(20) DEFAULT NULL COMMENT '账户状态:0-禁用，1-启用',
  `income_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '总收入',
  `expense_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '总支出',
  `credit_amount` decimal(18,2) NOT NULL DEFAULT '0.00' COMMENT '欠款金额',
  `seq` bigint NOT NULL DEFAULT '0' COMMENT ' userId、account_type, seq建立唯一索引',
  `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
  `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账户表';


CREATE TABLE `tb_account_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
    `account_id` bigint NOT NULL COMMENT '账户id',
    `request_no` varchar(64)  DEFAULT NULL COMMENT '流水号',
    `order_no` varchar(64)  NOT NULL COMMENT '订单号',
    `acc_no` varchar(32)  DEFAULT '' COMMENT '账户编号',
    `user_id` bigint NOT NULL COMMENT '用户id',
    `account_type` tinyint NOT NULL COMMENT '账户类型: 1-内部 2-外部 3-超管 ',
    `other_account` varchar(32)  DEFAULT NULL COMMENT '对方账户id',
    `other_account_type` tinyint DEFAULT '0' COMMENT '对方账户类型',
    `action_type` tinyint NOT NULL COMMENT '交易类型: 1-转入 2-转出 3-消费 4-充值 5-退款 6-冲正 7-发放撤回 8-借款 9-还款',
    `prod_type` varchar(32)  NOT NULL COMMENT '业务类型: 红包-001 转账-002',
    `trans_amount` decimal(18,2) NOT NULL COMMENT '交易金额',
    `balance` decimal(18,2) NOT NULL COMMENT '交易后余额',
    `source` varchar(32)  DEFAULT NULL COMMENT '数据来源',
    `app_id` varchar(32)  DEFAULT NULL COMMENT 'app_id',
    `remark` varchar(256)  DEFAULT NULL COMMENT '备注',
    `seq` bigint NOT NULL COMMENT '流水号',
    `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uniq_request` (`request_no`,`source`,`account_id`),
    KEY `idx_ct` (`create_time`),
    KEY `idx_uact` (`user_id`,`account_type`,`create_time`),
    KEY `idx_prod_type` (`prod_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户流水表';



CREATE TABLE `tb_merchant` (
    `id` bigint NOT NULL COMMENT '商户账户id',
    `acc_no` varchar(32)  NOT NULL COMMENT '账户号',
    `source` varchar(32)  NOT NULL COMMENT '来源',
    `merchant_no` varchar(32)  NOT NULL COMMENT '商户号',
    `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
    `account_type` tinyint(4) NOT NULL DEFAULT '1' COMMENT '账户类型: 20-发放 21-结算 22-商户账户 ',
    `prod_type` varchar(32)  NOT NULL DEFAULT '' COMMENT '产品类型',
    `balance` decimal(18,2) NOT NULL DEFAULT '0.0' COMMENT '账户余额',
    `freeze_amount` decimal(18,2) NOT NULL DEFAULT '0.0' COMMENT '冻结金额',
    `income_amount` decimal(18,2) DEFAULT '0.0' COMMENT '累计入账',
    `expense_amount` decimal(18,2) DEFAULT '0.0' COMMENT '累计支出',
    `apply_amount` decimal(18,2) DEFAULT '0.0' COMMENT '累计申请',
    `reversal_amount` decimal(18,2) DEFAULT '0.0' COMMENT '累计冲正',
    `back_amount` decimal(18,2) DEFAULT '0.0' COMMENT '累计撤回',
    `settle_amount` decimal(18,2) DEFAULT '0.0' COMMENT '累计结算',
    `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `seq` bigint NOT NULL DEFAULT '0' COMMENT '序号',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_acc` (`acc_no`) USING BTREE,
    UNIQUE KEY `uk_mer_seq` (`merchant_no`,`account_type`,`seq`) USING BTREE,
    KEY `idx_meracc` (`merchant_no`,`account_type`,`prod_type`,`source`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户账户表' ;


CREATE TABLE `tb_merchant_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易流水id',
    `account_id` bigint NOT NULL COMMENT '账户id',
    `merchant_no` varchar(32)  NOT NULL DEFAULT '' COMMENT '商户号',
    `acc_no` varchar(32)  NOT NULL DEFAULT '' COMMENT '账户号',
    `account_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '账户类型：0-发放账户 1-结算账户 2-商户账户',
    `request_no` varchar(64)  NOT NULL COMMENT '请求号',
    `order_no` varchar(64)  NOT NULL COMMENT '业务订单号',
    `other_account` varchar(32)  NOT NULL DEFAULT '' COMMENT '对方账户id',
    `other_account_type` varchar(32)  NOT NULL DEFAULT '' COMMENT '账户类型',
    `action_type` tinyint(4) NOT NULL DEFAULT '0' COMMENT '交易类型：0-转出 1-转入 2-出账 3-入账 4-冲正 5-退款 6-撤回',
    `prod_type` varchar(32)  DEFAULT '' COMMENT '业务类型',
    `trans_amount` decimal(18,2) NOT NULL DEFAULT '0.0' COMMENT '交易金额',
    `balance` decimal(18,2) NOT NULL DEFAULT '0.0' COMMENT '交易后余额',
    `remark` varchar(256)  DEFAULT '' COMMENT '备注',
    `source` varchar(32)  NOT NULL DEFAULT '' COMMENT '来源',
    `app_id` varchar(32)  DEFAULT '' COMMENT '业务系统Id',
    `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_rs` (`request_no`,`source`,`account_id`) USING BTREE,
    KEY `idx_mer_no` (`merchant_no`,`create_time`) USING BTREE,
    KEY `idx_acc` (`account_id`,`create_time`) USING BTREE,
    KEY `idx_accNo` (`acc_no`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商户账户流水表';


CREATE TABLE `tb_freeze` (
    `id` bigint unsigned NOT NULL COMMENT 'ID',
    `account_id` bigint NOT NULL COMMENT '账户ID',
    `freeze_type` varchar(32)  NOT NULL DEFAULT '0' COMMENT '冻结类型：2-转账冻结、3-结算冻结',
    `freeze_amount` decimal(18,2) unsigned NOT NULL DEFAULT '0.0' COMMENT '冻结金额',
    `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    `update_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6) COMMENT '更新时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_freeze_account` (`account_id`,`freeze_type`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='专款冻结表';

CREATE TABLE `tb_freeze_log` (
    `id` bigint NOT NULL COMMENT 'ID',
    `account_id` bigint NOT NULL COMMENT '账户ID',
    `action_type` tinyint(1) NOT NULL DEFAULT '0' COMMENT '交易类型：0-冻结 1-解冻 2-解冻出账 3-入账冻结',
    `acc_no` varchar(32)  NOT NULL DEFAULT '' COMMENT '账户号',
    `freeze_type` varchar(20)  NOT NULL DEFAULT '0' COMMENT '冻结类型',
    `freeze_amount` decimal(18,2) NOT NULL DEFAULT '0.0' COMMENT '冻结金额',
    `request_no` varchar(32)  NOT NULL COMMENT '请求号',
    `order_no` varchar(64)  NOT NULL COMMENT '业务订单号',
    `prod_type` varchar(32)  NOT NULL DEFAULT '' COMMENT '业务类型',
    `app_id` varchar(32)  NOT NULL DEFAULT '' COMMENT '业务系统ID',
    `source` varchar(32)  NOT NULL DEFAULT '' COMMENT '来源',
    `remark` varchar(256)  DEFAULT '' COMMENT '备注',
    `create_time` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '创建时间',
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE KEY `uk_freezelog` (`request_no`,`source`,`account_id`) USING BTREE,
    KEY `idx_acc` (`account_id`,`create_time`) USING BTREE,
    KEY `idx_acc_no` (`acc_no`,`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4  COMMENT='冻结流水表';





