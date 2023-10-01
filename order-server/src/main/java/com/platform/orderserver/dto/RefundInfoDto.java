package com.platform.orderserver.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 退款信息对象
 */
@Data
public class RefundInfoDto implements Serializable {

    private static final long serialVersionUID = 3048710231855541292L;

    private Long id;

    private String orderNo;

    private String status;

    /**
     * 退款单
     */
    private String refundNo;


    /**
     * 支付方式
     */
    private String payType;

    /**
     * 退款原因
     */
    private String reason;


    /**
     * 退款金额交易金额
     */
    private BigDecimal amount;

    /**
     * 原订单金额
     */
    private BigDecimal orderAmount;

    /**
     * 退款实体报文
     */
    private String content;





}
