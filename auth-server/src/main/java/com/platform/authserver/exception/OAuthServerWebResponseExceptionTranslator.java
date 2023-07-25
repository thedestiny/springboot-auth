package com.platform.authserver.exception;

import com.platform.authcommon.common.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

/**
 * @author huweihua
 * 自定义异常翻译器，针对用户名、密码异常，授权类型不支持的异常进行处理
 */
@SuppressWarnings("ALL")
public class OAuthServerWebResponseExceptionTranslator implements WebResponseExceptionTranslator{
    /**
     * 业务处理方法，重写这个方法返回客户端信息
     */
    @Override
    public ResponseEntity<Result> translate(Exception e){
        Result resultMsg = doTranslateHandler(e);
        return new ResponseEntity<>(resultMsg, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 根据异常定制返回信息
     * TODO 自己根据业务封装
     */
    private Result doTranslateHandler(Exception e) {
        //初始值，系统错误，
//        ResultCode resultCode = ResultCode.UNAUTHORIZED;
//        //判断异常，不支持的认证方式
//        if(e instanceof UnsupportedGrantTypeException){
//            resultCode = ResultCode.UNSUPPORTED_GRANT_TYPE;
//            //用户名或密码异常
//        }else if(e instanceof InvalidGrantException){
//            resultCode = ResultCode.USERNAME_OR_PASSWORD_ERROR;
//        }
        return Result.failed(e.getMessage());
    }
}
