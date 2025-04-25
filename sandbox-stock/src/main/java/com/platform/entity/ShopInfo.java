package com.platform.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-25 3:01 PM
 */
@Data
public class ShopInfo implements Serializable {


    private Long id;

    private String shopName;

    /**
     * 经度
     */
    private String longitude;

    /**
     * 详细地址
     */
    private String detail;

    /**
     * 评分
     */
    private BigDecimal score;

    /**
     * 简介
     */
    private String brief;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 维度
     */
    private String latitude;

    // 营业开始时间
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date startTime;


    // 营业结束时间
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    private Date endTime;

}
