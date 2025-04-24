package com.platform.pojo.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2024-12-13 6:55 PM
 */


@Data
@AllArgsConstructor
public class TimeNode {

    @ApiModelProperty(value = "时间点")
    private String time;

    @ApiModelProperty(value = "0 不可用, 1 可用")
    private Integer status;

    private String msg;





}
