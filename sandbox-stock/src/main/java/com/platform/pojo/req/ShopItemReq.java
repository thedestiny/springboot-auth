package com.platform.pojo.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-25 1:01 PM
 */

@Data
public class ShopItemReq implements Serializable {


    private static final long serialVersionUID = -6325292250237860253L;

    @ApiModelProperty(value = "项目id")
    private Long itemId;

    @ApiModelProperty(value = "店铺id")
    private Long shopId;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "预约开始时间 2024-12-02 09:30")
    private Date startTime;

    @ApiModelProperty(value = "城市代码")
    private Integer city;

    @ApiModelProperty(value = "经度")
    private String longitude;

    @ApiModelProperty(value = "维度")
    private String latitude;



}
