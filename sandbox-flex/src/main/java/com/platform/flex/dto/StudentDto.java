package com.platform.flex.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Description
 * @Date 2023-11-23 10:58 AM
 */

@Data
public class StudentDto implements Serializable {

    private static final long serialVersionUID = 7409429509824001317L;

    private Long id;

    private String username;

    private String address;

    private String idCard;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;


    private Integer status;

    private String userId;

    private BigDecimal balance;










}
