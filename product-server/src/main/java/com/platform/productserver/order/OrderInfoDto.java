package com.platform.productserver.order;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Date 2023-09-07 8:26 PM
 */
@Data
public class OrderInfoDto implements Serializable {

    private static final long serialVersionUID = -306584714990267992L;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "交易金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商户号")
    private String merNo;

    public OrderInfoDto() {
    }

    public OrderInfoDto(String userId, BigDecimal amount, String orderNo) {
        this.userId = userId;
        this.amount = amount;
        this.orderNo = orderNo;
    }
}
