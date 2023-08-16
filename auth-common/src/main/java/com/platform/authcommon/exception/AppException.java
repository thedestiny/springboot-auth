package com.platform.authcommon.exception;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description 系统自定义异常
 * @Date 2023-08-15 9:08 AM
 */
@Data
public class AppException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -3507072569977432831L;


    private String msg;
    private Integer code = 500;

    public AppException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public AppException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public AppException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public AppException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }




}
