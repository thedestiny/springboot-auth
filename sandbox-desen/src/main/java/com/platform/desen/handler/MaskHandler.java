package com.platform.desen.handler;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 10:03 AM
 */
public interface MaskHandler {

    // 正则匹配方式
    String regrex(String str);


    // 关键字匹配方式
    String keyword(String str);

}
