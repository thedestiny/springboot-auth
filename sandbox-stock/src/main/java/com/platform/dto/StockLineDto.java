package com.platform.dto;


import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class StockLineDto implements Serializable {

    private static final long serialVersionUID = 5673912298211556416L;

    // 日期
    private String date;

    // 代码
    private String code;
    // 名称
    private String name;

    // 变动比例
    private BigDecimal rate;

    // 价格
    private BigDecimal price;



}
