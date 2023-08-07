

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
     `update_time` datetime DEFAULT NULL COMMENT '更新时间',
     `focus` int(11) DEFAULT NULL COMMENT '关注度',
     `market_capital` decimal(11,3) DEFAULT NULL COMMENT '总市值(亿元)',
     `turnover_rate` decimal(11,3) DEFAULT NULL COMMENT '换手率(%)',
     PRIMARY KEY (`id`)
) DEFAULT CHARSET = utf8mb4 COMMENT = 'stock信息表';
