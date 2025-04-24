package com.platform.pojo.res;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ShopInfoRes implements Serializable {

    private static final long serialVersionUID = -6501666167697050645L;


    @ApiModelProperty(value = "店铺id")
    private Long id;

    @ApiModelProperty(value = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "店铺地址")
    private String address;

    /** 入驻时间 */
    @ApiModelProperty(value = "入驻时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date settleDate;

    /** 经度 */
    @ApiModelProperty(value = "店铺经度")
    private String longitude;

    /** 维度 */
    @ApiModelProperty(value = "店铺维度")
    private String latitude;

    /** 营业时间 */
    @ApiModelProperty(value = "营业时间")
    private String businessHours;

    @JsonFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "营业开始时间")
    private Date startTime;

    @JsonFormat(pattern = "HH:mm")
    @ApiModelProperty(value = "营业结束时间")
    private Date endTime;

}
