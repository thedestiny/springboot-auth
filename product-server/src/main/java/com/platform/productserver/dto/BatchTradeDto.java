package com.platform.productserver.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description 批量操作对象
 * @Date 2023-09-19 2:25 PM
 */
@Data
public class BatchTradeDto implements Serializable {

    private static final long serialVersionUID = 592624151719492769L;

    @ApiModelProperty(value = "商户号")
    private String merchantNo;

    @ApiModelProperty(value = "账户类型")
    private Integer accountType;

    // 记否记录欠款
    private Boolean credit = false;

    // 交易金额
    @ApiModelProperty(value = "交易总金额")
    private BigDecimal amount;

    // 交易金额
    @ApiModelProperty(value = "交易金额")
    @NotEmpty(message = "交易列表不能为空")
    private List<BatchUserTradeDto> tradeList;





}
