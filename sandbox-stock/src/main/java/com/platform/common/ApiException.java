package com.platform.common;


import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String msg;

    private Integer code = 500;

    public ApiException(String errmsg) {
        super(errmsg);
        this.msg = errmsg;
    }

    public ApiException(String errmsg, Throwable e) {
        super(errmsg, e);
        this.msg = errmsg;
    }

    public ApiException(String errmsg, Integer errno) {
        super(errmsg);
        this.msg = errmsg;
        this.code = errno;
    }

    public ApiException(String errmsg, int errno, Throwable e) {
        super(errmsg, e);
        this.msg = errmsg;
        this.code = errno;
    }


}
