package com.platform.messsage.utils;

import cn.hutool.core.lang.Snowflake;

/**
 *
 * @Description 订单号生成器
 * @Date 2023-12-11 10:51 上午
 */

public class OrderNoUtils {


    private static Snowflake flake;

    static {
        flake = new Snowflake();
    }


    public static String idStr(){
        return flake.nextIdStr();
    }

    public static Long id(){
        return flake.nextId();
    }
}
