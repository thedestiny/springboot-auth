package com.platform.productserver.utils;

import com.platform.productserver.order.OrderInfoDto;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * @Description
 * @Date 2023-10-11 3:03 PM
 */
public class OptionalTest {


    public static void main(String[] args) throws NoSuchMethodException {

        String mobile = "13849869912";
        boolean b = Pattern.matches("^1[0-9]{10}$", mobile);
        System.out.println(b);




        OrderInfoDto dto1 = new OrderInfoDto("123", BigDecimal.valueOf(5), "456");
        //dto1.setMerNo("345");

        Optional.ofNullable(dto1.getMerNo()).ifPresent(node -> {
            System.out.println("ddd  " + node);
        });

        String dd = Optional.ofNullable(dto1.getMerNo()).orElse("34");
        System.out.println(dd);

        test01("345");


    }

    public static void test01(String test) throws NoSuchMethodException {

        ExCatchUtils utils = new ExCatchUtils();
        utils.doCatch(new RuntimeException("dddd"),"消息" );


       // .doCatch(new RuntimeException("dddd"),"消息" );
    }


}
