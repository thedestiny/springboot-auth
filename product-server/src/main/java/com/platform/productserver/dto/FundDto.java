package com.platform.productserver.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class FundDto implements Serializable {

    private static final long serialVersionUID = -1543159541992014353L;

    // 基金代码 0
    private String code;
    // 基金名称 1
    private String name;
    // 更新日期 3
    private String updateDate;
    // 近一周涨幅 7
    private BigDecimal week;
    // 近1月 8
    private BigDecimal month;
    // 近三月 9
    private BigDecimal month3;
    // 近6月 10
    private BigDecimal half;
    // 近一年 11
    private BigDecimal year;
    // 今年来 14
    private BigDecimal since;
    // 手续费 22
    private BigDecimal fee;

    // 基金类型 大类
    private String type;

    // 基金类型
    private String fundType;
    // 基金经理
    private String manager;
    // 基金公司
    private String company;
    // 发行日期
    private String issue;
    // 业绩比较基准
    private String baseline;
    // 跟踪标的
    private String tracking;

    // 基金规模 亿元
    private String fundSize;
    // 基金份额 亿份
    private String shareSize;


}
