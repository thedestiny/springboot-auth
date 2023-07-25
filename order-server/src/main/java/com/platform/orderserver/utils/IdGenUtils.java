package com.platform.orderserver.utils;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IdGenUtils {


    private static final Snowflake flake;

    static {
        flake = new Snowflake();
    }


    public static String orderNo(){
        return "T" + flake.nextIdStr();
    }


    public static String id() {
        return  flake.nextIdStr();
    }
}
