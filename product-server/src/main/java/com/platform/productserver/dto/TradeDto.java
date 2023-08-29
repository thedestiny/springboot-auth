package com.platform.productserver.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TradeDto implements Serializable {

    private static final long serialVersionUID = 2787588891032132609L;

    // 交易账户id
    private Long transId;

    @ApiModelProperty(value = "userId")
    private String userId;

    @ApiModelProperty(value = "账户类型")
    private Integer accountType;

    // 交易金额
    @ApiModelProperty(value = "交易金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "流水号")
    private String requestNo;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    // 对方账户
    private String otherAccount;

    private Integer otherAccountType;
    // 交易类型
    @ApiModelProperty(value = "业务类型 1 提现 2 充值 3 消费 4 分享")
    private String prodType;
    // 交易类型
    @ApiModelProperty(value = "交易类型: 1--转入 2-转出")
    private Integer transType;

    @ApiModelProperty(value = "来源信息")
    private String source;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "appId")
    private String appId;

    // 记否记录欠款
    private Boolean credit = false;



}
