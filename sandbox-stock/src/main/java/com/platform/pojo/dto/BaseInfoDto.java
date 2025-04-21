package com.platform.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2024-11-28 5:45 PM
 */

@Data
public class BaseInfoDto implements Serializable {

    private static final long serialVersionUID = 3048710231855541292L;

    // id
    private Long id;

    // 订单Id
    private Long orderId;

    // 订单号
    private String orderNo;

    // 订单状态
    private String status;

    // 预约单号
    private String resId;

    // 预支付单号
    private String prepayId;

    // 失效时间
    private Date expireTime;

    private String msg;



}
