package com.platform.pojo.res;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-25 1:00 PM
 */

@Data
public class MasseurInfoRes implements Serializable  {

    @ApiModelProperty(value = "技师id")
    private Long id;

    @ApiModelProperty(value = "技师头像")
    private String avatar;

    @ApiModelProperty(value = "技师昵称")
    private String nickName;

    @ApiModelProperty(value = "技师名称")
    private String realName;

    @ApiModelProperty(value = "接单量")
    private Integer orderNum;

    @ApiModelProperty(value = "评分")
    private BigDecimal score;

    @ApiModelProperty(value = "等级")
    private Integer grade;

    @ApiModelProperty(value = "简介")
    private String brief;

    @ApiModelProperty(value = "0-空闲,1-繁忙,需要读取服务时间,2-已约满")
    private Integer idle = 0;

    @ApiModelProperty(value = "服务开始时间")
    private Date startTime;

    @ApiModelProperty(value = "技师关联项目信息")
    private List<ItemInfoRes> itemList;




}
