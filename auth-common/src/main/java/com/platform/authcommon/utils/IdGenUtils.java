package com.platform.authcommon.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

/**
 * id 生成器
 */
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


    public static Long pid() {
        return  flake.nextId();
    }

}
