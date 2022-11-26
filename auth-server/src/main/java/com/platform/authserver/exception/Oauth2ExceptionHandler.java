package com.platform.authserver.exception;

import com.platform.authcommon.common.Result;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局处理Oauth2抛出的异常
 */
@RestControllerAdvice
public class Oauth2ExceptionHandler {

    @ExceptionHandler(value = OAuth2Exception.class)
    public Result handleOauth2(OAuth2Exception e) {
        return Result.failed(e.getMessage());
    }
}
