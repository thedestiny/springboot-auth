package com.platform.orderserver.utils;

import cn.hutool.core.lang.Snowflake;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-05-28 6:11 PM
 */

@Slf4j
public class IdGenUtils {

    // 1, 1
    // 终端ID workerId, 数据中心ID dataCenterId
    private static final Snowflake SNOWFLAKE = new Snowflake();

    // 生成订单号
    public static String genOrderNo() {
        return "ORDER_" + SNOWFLAKE.nextIdStr();
    }

    // 生成唯一请求号
    public static String genRequestId() {
        return SNOWFLAKE.nextIdStr();
    }

}
