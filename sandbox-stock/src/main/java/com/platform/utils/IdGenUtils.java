package com.platform.utils;

import cn.hutool.core.lang.Snowflake;

public class IdGenUtils {

    public static final Snowflake flake;

    static {
        flake = new Snowflake();
    }

    private IdGenUtils() {
    }

    public static Long getId(){
        return flake.nextId();
    }


    public static String getIdStr(){
        return flake.nextIdStr();
    }

}
