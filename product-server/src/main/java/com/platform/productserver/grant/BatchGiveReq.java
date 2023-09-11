package com.platform.productserver.grant;

import com.platform.authcommon.base.BaseReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 积分分发请求-批量
 * @Date 2023-09-07 4:37 PM
 */

@Data
public class BatchGiveReq extends BaseReq implements Serializable {

    private static final long serialVersionUID = -265042973076246885L;

    @ApiModelProperty("批次号")
    private String batchNo;

    @ApiModelProperty("回调地址")
    private String callBackUrl;

    @ApiModelProperty(value = "交易订单号")
    @NotBlank(message = "交易订单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "活动类型")
    private String activityType;

    @ApiModelProperty("活动编号")
    private String activityNo;

    @ApiModelProperty(value = "出账商户号")
    @NotBlank(message = "出账商户号不能为空")
    private String merchantNo;

    @ApiModelProperty(value = "出账账户编号")
    @NotBlank(message = "出账账户编号不能为空")
    private String outAccNo;

    @ApiModelProperty(value = "出账账户类型")
    @NotNull(message = "出账账号类型不能为空")
    private Integer outAccNoType;

    @ApiModelProperty(value = "分发金额")
    @NotNull(message = "分发金额不能为空")
    @Digits(integer = 14, fraction = 2, message = "分发金额不合法")
    private BigDecimal amount;

    @ApiModelProperty(value = "业务类型")
    @NotBlank(message = "业务类型不能为空")
    private String prodType;

    @NotBlank(message = "业务系统编号不能为空")
    @ApiModelProperty("业务系统编号")
    private String appId;

    @ApiModelProperty("积分到期时间 格式：yyyy-MM-dd")
    private String expireTime;

    @ApiModelProperty("渠道信息")
    private String channel;

    @ApiModelProperty("分发数据类型")
    private Integer grantDataType = 1;

    @Valid
    @NotEmpty(message = "发放列表不能为空")
    private List<GiveUserDto> userList;





}
