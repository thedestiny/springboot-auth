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
public class FreezeTradeDto extends FreezeDto implements Serializable {

    private static final long serialVersionUID = 8380399281324399606L;

    @ApiModelProperty("对方账户")
    private String otherAccount;

    @ApiModelProperty("对方账户类型")
    private Integer otherAccountType;

}
