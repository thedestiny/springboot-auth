package com.platform.productserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_fund_info")
public class FundInfo implements Serializable {

    private static final long serialVersionUID = 1026463126361594215L;


    /**
     * code
     */
    @TableId(value = "code", type = IdType.NONE)
    private String code;

    /**
     * 基金名称
     */
    private String name;

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
     * 近一年涨幅
     */
    private BigDecimal year;

    /**
     * 今年来涨幅
     */
    private BigDecimal since;

    /**
     * 基金大类
     */
    private String type;

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
     * 手续费
     */
    private String fee;

    // 赎回费率
    private String sellFee;
    // 买入费率
    private String buyFee;

}
