package com.platform.productserver.utils;

import com.platform.authcommon.exception.AppException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Description 异常处理器
 * @Date 2023-10-11 3:56 PM
 */

@Slf4j
public class ExCatchUtils {


    public static void doCatch(Exception ex, String msg) {

        if (ex instanceof AppException) {
            log.error("error is {}", ((AppException) ex).getMsg(), ex);
        } else {
            log.error("error is {}", ex.getMessage(), ex);
        }


    }


}
