package com.platform.flex.utils;

import cn.hutool.core.lang.Snowflake;

/**
 * @Description
 * @Date 2023-12-01 2:54 PM
 */
public class IdGenutils {



    private static final Snowflake flake;


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
