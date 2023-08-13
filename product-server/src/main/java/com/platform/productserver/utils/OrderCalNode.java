package com.platform.productserver.utils;

import cn.hutool.core.util.NumberUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
public class OrderCalNode implements Serializable {

    private static final long serialVersionUID = -4305779956803150142L;

    // 订单id
    private String keyId;
    // 子单总金额
    private BigDecimal total;
    // 珑珠金额
    private BigDecimal point;
    // 珑珠实付
    private BigDecimal pointAct;
    // 现金金额
    private BigDecimal cash;
    // 膨胀金
    private BigDecimal expand;
    // 立减金额
    private BigDecimal inflation;


    public void calculateCashPoint() {
        this.cash = NumberUtil.sub(total, pointAct, expand, inflation);
        this.point = NumberUtil.add(pointAct, expand, inflation);
    }

}
