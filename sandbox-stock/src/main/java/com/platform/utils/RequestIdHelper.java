package com.platform.utils;

import cn.hutool.core.lang.Snowflake;

public class RequestIdHelper {



    public static Snowflake flake;

    static {
        flake = new Snowflake();
    }


    public static String generate() {
        return flake.nextIdStr();
    }
}
