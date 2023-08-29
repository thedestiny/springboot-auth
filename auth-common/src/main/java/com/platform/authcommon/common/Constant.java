package com.platform.authcommon.common;

import cn.hutool.core.date.DateUtil;

/**
 * @Description 常量
 * @Date 2023-08-15 10:52 AM
 */
public class Constant {


    public static final String EQUALS_LINE = "=";
    public static final String REDIS_SPLIT_LINE = EQUALS_LINE;
    public static final String PLUS = "+";
    public static final String REDIS_KEY_CONNECTION_LINE = PLUS;
    public static final String SPLIT_PLUS = "\\+";
    public static final String REDIS_SPLIT_KEY_CONNECTION_LINE = SPLIT_PLUS;

    // 红包业务前缀
    public final static String RED_PKG_PREFIX = "RED:";
    // 分布式锁的 redis key
    public final static String DIS_KEY = "DIS_KEY:";

    public final static Long DEFAULT_SEQ = 0L;
    // 单次查询数据量
    public final static Integer BATCH_SIZE = 200;


    public static void main(String[] args) {

        String time = DateUtil.format(DateUtil.date(), "yyMMdd");
        System.out.println(time);
        String format = String.format("%05d", 1);
        System.out.println(format);
    }

}
