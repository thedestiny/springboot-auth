package com.platform.productserver.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 冻结操作
 */
@Data
public class FreezeDto implements Serializable {

    private static final long serialVersionUID = -2603148210814297920L;

    @NotBlank(message = "账户编号不能为空")
    @ApiModelProperty("账户编号")
    private String accNo;

    @ApiModelProperty("活动类型")
    private String activityType;

    @Length(max = 32, message = "请求号长度不能超过32个字符")
    @ApiModelProperty("请求号")
    private String requestNo;

    @ApiModelProperty("订单号")
    private String orderNo;

    @NotNull(message = "交易金额不能为空")
    @ApiModelProperty("交易金额/冻结金额/解冻金额")
    @Digits(integer = 18, fraction = 2, message = "交易金额格式有误")
    private BigDecimal amount;

    @NotBlank(message = "业务类型不能为空")
    @ApiModelProperty("业务类型")
    private String prodType;

    @NotBlank(message = "业务系统Id不能为空")
    @ApiModelProperty("业务系统Id")
    private String appId;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("来源")
    private String source;

    @Length(max = 32, message = "原冻结单号请求号长度不能超过32个字符")
    @ApiModelProperty("原冻结单号")
    private String origRequestNo;


}
