package com.platform.authcommon.exception;

import com.platform.authcommon.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;

/**
 * 异常处理器
 */
@Slf4j
@Component
@RestControllerAdvice
public class AppExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(AppException.class)
    public Result handleRRException(AppException e) {
        Result r = new Result();
        r.setCode(e.getCode());
        r.setMessage(e.getMessage());
        return r;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return Result.failed("数据库中已存在该记录");
    }

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.failed();
    }
}
