package com.platform.entity;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName(value = "tb_stock_info")
public class StockInfo implements Serializable {

    private static final long serialVersionUID = 9014134377958817083L;

    // 股票代码
    @TableId(value = "id", type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 成交额(亿元)
     */
    private BigDecimal amount;

    /**
     * 振幅,百分比
     */
    private BigDecimal amplitude;

    /**
     * 涨跌(元)
     */
    private BigDecimal chg;

    /**
     * 当前价格
     */
    private BigDecimal current;

    /**
     * 当年涨跌(%)
     */
    private BigDecimal currentYearPercent;

    /**
     * 股息率ttm(%)
     */
    private BigDecimal dividendYield;

    /**
     * 每股收益(元)
     */
    private BigDecimal eps;

    /**
     * 流通市值(亿元)
     */
    private BigDecimal floatMarketCapital;

    // market_capital
    private BigDecimal marketCapital;
    // turnover_rate
    private BigDecimal turnoverRate;

    /**
     * 流通股(亿)
     */
    private BigDecimal floatShares;

    /**
     * 总股本(亿)
     */
    private BigDecimal totalShares;

    /**
     * 市净率
     */
    private BigDecimal pb;

    /**
     * 市净率(ttm)
     */
    private BigDecimal pbTtm;

    /**
     * 市盈率(ttm)
     */
    private BigDecimal peTtm;

    /**
     * 当天涨跌幅(%)
     */
    private BigDecimal percent;

    /**
     * 市盈率(动态)
     */
    private BigDecimal pe;

    /**
     * 每股净资产
     */
    private BigDecimal netValue;

    /**
     * 52周最高
     */
    private BigDecimal highYear;

    /**
     * 52周最低
     */
    private BigDecimal lowYear;

    /**
     * 更新时间
     */
    private DateTime updateTime;
    /**
     * 关注度
     */
    private Integer focus;

    /**
     * 是否选择
     */
    private Integer choice;
    // issue_date_ts
    // limitup_days
    // 上市时间
    private String issue;


    public static void main(String[] args) {


        String dt = "2023年04月14日";
        String replace = ReUtil.replaceAll(dt, "[年月]", "-").replace("日", "");
        System.out.println(replace);
    }



}
