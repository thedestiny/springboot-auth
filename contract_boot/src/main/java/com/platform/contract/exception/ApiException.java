package com.platform.contract.exception;

import lombok.Data;

@Data
public class ApiException extends RuntimeException {

    private static final long serialVersionUID = -2974697440346859888L;

    private ApiCodeEnum apiCodeEnum;
    private String memo;
    private Object[] params;

    public ApiException(ApiCodeEnum respCodeEnum, String memo, Object... params) {
        super(respCodeEnum.getCode() + "," + memo);
        this.apiCodeEnum = respCodeEnum;
        this.memo = memo;
        this.params = params;
    }
}
