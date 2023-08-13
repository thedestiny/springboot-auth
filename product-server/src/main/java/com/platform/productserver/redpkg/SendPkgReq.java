package com.platform.productserver.redpkg;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红包发送请求参数
 */
@Data
public class SendPkgReq implements Serializable {

    private static final long serialVersionUID = -1823281196761885912L;

    // 红包单号
    private String orderNo;
    // 发送人ID
    private String senderId;
    // 红包总金额
    private BigDecimal total;
    // 红包总金额
    private Integer num;
    // 红包类型 1-个人红包 2-群红包平分模式 2群红包拼手气
    private Integer redType;

}
