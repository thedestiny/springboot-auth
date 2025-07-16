package com.platform.entity;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

/**
 * etf 组信息
 */

@Data
public class ETfHomeInfo {



    @JSONField(name = "code")
    private String code;


    @JSONField(name = "close")
    private BigDecimal close;

    /**
     * 基金公司
     */
    @JSONField(name = "fundfirm")
    private String company;

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "index")
    private String index;

    /**
     * 成交量
     */
    private BigDecimal avgamount;

    /**
     * 上市日期
     */
    private String listdate;

    /**
     * 当月涨幅
     */
    private BigDecimal m1;

    /**
     * 费率
     */
    private String mtexp;


    private Integer premium;

    /**
     * 当天涨幅
     */
    private BigDecimal rate;

    /**
     * 规模
     */
    private BigDecimal scale;

    /**
     * 标签
     */
    private String tag;

    /**
     * 年初至今
     */
    private BigDecimal y0;
    /**
     * 近一年
     */
    private BigDecimal y1;
    /**
     * 近3年
     */
    private BigDecimal y3;
    /**
     * 近5年
     */
    private BigDecimal y5;

}
