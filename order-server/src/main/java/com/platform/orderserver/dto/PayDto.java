package com.platform.orderserver.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayDto {

    /**
     * 商品信息
     */
    private String subject;
    /**
     * 商品金额
     */
    private BigDecimal amount;

    /**
     * storeId
     */
    private String storeId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 支付方式 1 支付宝 2 微信 3 银联
     */
    private Integer payType;

}
