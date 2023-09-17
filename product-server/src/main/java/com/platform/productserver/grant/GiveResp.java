package com.platform.productserver.grant;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiOperation(value = "积分分发-响应")
public class GiveResp implements Serializable {

    private static final long serialVersionUID = 6988735868201041339L;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty("请求号")
    private String requestNo;

    @ApiModelProperty("批次号")
    private String batchNo;



}
