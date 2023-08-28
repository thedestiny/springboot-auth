package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基金基本信息表
 * @author destiny
 * @since 2023-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_etf_info")
public class EtfInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * code
     */
    @TableId(value = "code", type = IdType.NONE)
    private String code;

    /**
     * 基金名称
     */
    private String name;

    // 单位净值
    private BigDecimal price;

    /**
     * 基金简称
     */
    private String brief;

    /**
     * 近一周涨幅
     */
    private BigDecimal week;

    /**
     * 近一月涨幅
     */
    private BigDecimal month;

    /**
     * 近三月涨幅
     */
    private BigDecimal month3;

    /**
     * 近6月涨幅
     */
    private BigDecimal half;

    /**
     * 今年来
     */
    private BigDecimal year;

    /**
     * 近一年涨幅
     */
    private BigDecimal year1;

    /**
     * 近2年
     */
    private BigDecimal year2;
    /**
     * 近三年
     */
    private BigDecimal year3;


    /**
     * 成立以来涨幅
     */
    private BigDecimal since;

    /**
     * 基金类型
     */
    private String fundType;

    /**
     * 基金经理
     */
    private String manager;

    /**
     * 基金公司
     */
    private String company;

    /**
     * 发行日期
     */
    private String issue;

    /**
     * 业绩比较基准
     */
    private String baseline;

    /**
     * 跟踪标的
     */
    private String tracking;

    /**
     * 基金规模
     */
    private String fundSize;

    /**
     * 基金份额
     */
    private String shareSize;

    /**
     * 数据更新日期
     */
    private String updateDate;

    /**
     * 更新涨跌幅
     */
    private BigDecimal rate;



}
