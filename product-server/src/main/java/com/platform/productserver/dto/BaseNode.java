package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-09-20 5:12 PM
 */

@Data
public class BaseNode implements Serializable {



    private String requestNo;

    private Integer status;

    private String error;

}
