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


}
