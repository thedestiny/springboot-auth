package com.platform.orderserver.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-07-14 5:14 PM
 */
@Data
public class Order implements Serializable {

    private Long id;

    private String orderNo;

    private Integer status;

    private Date createTime;

    private Date updateTime;
}
