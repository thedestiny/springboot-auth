package com.platform.productserver.grant;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class GiveRefundReq implements Serializable {

    private static final long serialVersionUID = 699021280537226951L;

    @ApiModelProperty("原分发号")
    private String giveNo;

    @ApiModelProperty("撤回订单号")
    private String orderNo;

    @ApiModelProperty("撤回请求号")
    private String requestNo;

    @ApiModelProperty("撤回金额")
    private BigDecimal amount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("摘要")
    private String summary;

    // 撤回类型 1：撤回并销毁 2：仅撤回到账户余额
    private Integer refundType;
    // 是否允许欠款 0-否 1-是
    private Integer allowDebit;




}
