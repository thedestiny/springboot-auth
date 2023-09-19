package com.platform.productserver.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Date 2023-09-19 2:28 PM
 */
@Data
public class BatchUserTradeDto implements Serializable {

    private static final long serialVersionUID = -2536871777325078874L;

    // 对方账户和对方账户类型
    private String otherAccount;
    private Integer otherAccountType;

    // 交易金额
    @ApiModelProperty(value = "交易金额")
    private BigDecimal amount;

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

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "流水号")
    private String requestNo;

}
