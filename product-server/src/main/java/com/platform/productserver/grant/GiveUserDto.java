package com.platform.productserver.grant;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description 分发用户
 * @Date 2023-09-11 3:46 PM
 */
@Data
public class GiveUserDto implements Serializable {

    private static final long serialVersionUID = 5687533359445617682L;

    /**
     * 请求号
     */
    @NotBlank(message = "请求编号不能为空")
    @ApiModelProperty("请求号")
    private String requestNo;

    @ApiModelProperty("订单遍号")
    private String orderNo;

    @ApiModelProperty("用户ID")
    private String userId;

    @NotNull(message = "账户类型不能为空")
    @ApiModelProperty("账户类型：1-内部、2-外部、3-管理者")
    private Integer accountType;

    @ApiModelProperty("数据类型 1-toC 2-toB")
    private Integer dataType;

    @ApiModelProperty("业务类型")
    private String prodType;

    @NotNull(message = "分发金额不能为空")
    @Digits(integer = 18, fraction = 2, message = "整数位上限为16位，小数位上限为2位")
    @DecimalMin(value = "0.1", message = "必须大于等于0.1")
    @ApiModelProperty("发放数量 精确到小数点1位")
    private BigDecimal amount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("账单摘要")
    private String tradeSummary;
}
