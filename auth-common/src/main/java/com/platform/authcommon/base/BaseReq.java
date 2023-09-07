package com.platform.authcommon.base;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @Description 基本请求
 * @Date 2023-09-07 4:43 PM
 */
@Data
@ApiOperation(value = "基本请求")
public class BaseReq implements Serializable {

    private static final long serialVersionUID = 1026974910115244722L;

    @ApiModelProperty("请求时间戳")
    private Long timestamp;

    @ApiModelProperty("请求号")
    @NotBlank(message = "请求号不能为空")
    @Length(max = 50, message = "来源长度不能超过50个字符")
    private String requestNo;

    @ApiModelProperty("来源，默认为0")
    @NotBlank(message = "来源不能为空")
    @Length(max = 50, message = "请求号长度不能超过32个字符")
    private String source;

    @ApiModelProperty("备注")
    @Length(max = 300, message = "备注信息不能超过300个字符")
    private String remark;



}
