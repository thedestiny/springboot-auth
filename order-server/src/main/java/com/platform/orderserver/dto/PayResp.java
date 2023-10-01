package com.platform.orderserver.dto;

import lombok.Data;

import java.io.Serializable;

/**
 *
 *
 */

@Data
public class PayResp implements Serializable {

    private static final long serialVersionUID = -433134976441618568L;
    // 订单号
    private String orderNo;

    // 支付链接
    private String codeUrl;

    private String imageUrl;

    private String payType;




}
