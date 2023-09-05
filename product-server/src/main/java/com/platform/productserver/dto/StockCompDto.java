package com.platform.productserver.dto;

import com.platform.productserver.entity.StockInfo;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description stock 对比
 * @Date 2023-08-24 3:15 PM
 */

@Data
public class StockCompDto implements Serializable {

    private static final long serialVersionUID = 3354872856655422759L;

    private String code;

    private String name;

    private BigDecimal price;

    private BigDecimal rate;

    private List<StockInfo> list;

    public StockCompDto(String code, String name) {
        this.code = code;
        this.name = name;
    }
    public StockCompDto(String code, String name,List<StockInfo> list ) {
        this.code = code;
        this.name = name;
        this.list = list;
    }

    public StockCompDto() {
    }

    public static void main(String[] args) {








    }
}
