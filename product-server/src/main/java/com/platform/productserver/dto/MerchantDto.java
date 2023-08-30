package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 商户账户
 * @Date 2023-08-30 2:36 PM
 */
@Data
public class MerchantDto implements Serializable {

    private static final long serialVersionUID = -3020480500850328011L;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 账户类型 账户类型：30-发放账户 31-商户账户 32-结算账户
     */
    private Integer accountType;

    private String source;

    private String prodType;


    public static void main(String[] args) {

        String tdd = "1";
        String txt = "中";

        System.out.println(tdd.length());
        System.out.println(txt.length());

    }
}
