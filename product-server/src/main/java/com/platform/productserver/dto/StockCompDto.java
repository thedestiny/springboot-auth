package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description stock 对比
 * @Date 2023-08-24 3:15 PM
 */

@Data
public class StockCompDto implements Serializable {

    private static final long serialVersionUID = 3354872856655422759L;

    private String code;

    private String name;

    public StockCompDto(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public StockCompDto() {
    }

    public static void main(String[] args) {








    }
}
