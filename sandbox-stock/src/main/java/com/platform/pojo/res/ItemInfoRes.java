package com.platform.pojo.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2024-11-25 4:54 PM
 */

@Data
@ApiModel(value = "ItemInfoRes", description = "项目信息响应实体")
public class ItemInfoRes implements Serializable {

    private static final long serialVersionUID = 6073928029394576127L;

    @ApiModelProperty(value = "项目id")
    private Long id;

    @ApiModelProperty(value = "项目名称")
    private String itemName;

    @ApiModelProperty(value = "项目价格")
    private BigDecimal price;

    @ApiModelProperty(value = "技法")
    private String technique;

    @ApiModelProperty(value = "实际价格")
    private BigDecimal realPrice;

    /** 项目时长,单位为分钟 */
    @ApiModelProperty(value = "项目时长,单位为分钟")
    private Long costTime;

    @ApiModelProperty(value = "项目简介")
    private String brief;

    @ApiModelProperty(value = "介绍视频")
    private String introduceVideo;

    @ApiModelProperty(value = "介绍图片")
    private String introduceImg;

    @ApiModelProperty(value = "项目图片")
    private String itemImg;


    @ApiModelProperty(value = "L1技师项目价格")
    private BigDecimal price1;

    @ApiModelProperty(value = "L2技师项目价格")
    private BigDecimal price2;

    @ApiModelProperty(value = "L3技师项目价格")
    private BigDecimal price3;


}
