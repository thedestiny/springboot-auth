package com.platform.orderserver.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataReq implements Serializable {

    /**
     * 应用密文
     */
    private String appId;

    /**
     * 加密报文
     */
    private String encrypt;
    /**
     * 签名
     */
    private String sign;

}
