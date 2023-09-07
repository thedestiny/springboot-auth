package com.platform.authcommon.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDto implements Serializable {


    private static final long serialVersionUID = -8592616479203242217L;

    private String userId;

    private String orderNo;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime;

    private BigDecimal amount;

    private String name;

}
