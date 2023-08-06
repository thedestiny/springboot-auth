package com.platform.productserver.entity;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@TableName(value = "tb_stock_info")
public class Stock implements Serializable {

    // 股票代码
    @TableId(value = "id")
    private String id;
    // 股票名称
    private String name;
    // 当前价格
    private BigDecimal current;


    public static void main(String[] args) {

        BigDecimal bigDecimal = new BigDecimal("1.326072113E9");
        System.out.println(bigDecimal);
        int year = DateUtil.date().year();
        System.out.println(year);
        DateTime parse1 = DateUtil.parse("08-04", "MM-dd");
        System.out.println(parse1);
    }



}
