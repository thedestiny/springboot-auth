package com.platform.authcommon.exception;

import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 页面异常处理器
 */
@Slf4j
@Component
@ControllerAdvice
public class WebExceptionHandler {


    /**
     * 页面异常处理
     */
    @ExceptionHandler(AppException.class)
    public String handleAppException(AppException e) {
        String th = Throwables.getStackTraceAsString(e);
        log.info("error info {}", th);
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e) {
        String th = Throwables.getStackTraceAsString(e);
        log.info("error info {}", th);
        return "error";
    }
}
