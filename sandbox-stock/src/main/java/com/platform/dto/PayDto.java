package com.platform.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2024-11-26 10:55 AM
 */

@Data
public class PayDto implements Serializable {

    private static final long serialVersionUID = -4636797395382525258L;

    private String username;

    private Long consumerId;

    // 支付人唯一id
    private String openid;

    /**
     * 1 支付 2 退款
     */
    // @ApiModelProperty(value = "业务类型 1 支付 2 退款 ")
    private String dataType;

    // 支付类型
    // 支付类型 weixin-微信  alipay-支付宝
    private String payType;

    private String busType;

    // 订单号
    private String orderNo;

    // 订单id
    private Long orderId;


    // 客户端ip
    private String clientIp;


    // 退款单号
    private String refundNo;

    // 交易类型
    private String tradeType;

    // 订单标题 支付金额 支付链接 支付单号 状态
    private String title;

    // 订单金额
    private BigDecimal amount;

    // 退款金额
    private BigDecimal refund;

    private String codeUrl;

    // 支付单号
    private String payNo;

    /**
     * 交易单号
     * WxTradeState 0-未支付,1-支付成功,2-已关闭, 3-转入退款, 4-退款失败
     */
    private String status;

    // 订单状态
    private String orderStatus;

    /**
     * 内容
     */
    private String content;

    // 交易单id
    private Long id;

    private String attach;

    // 支付单类型
    private String orderType;

    private String mth = "jsapi";




}
