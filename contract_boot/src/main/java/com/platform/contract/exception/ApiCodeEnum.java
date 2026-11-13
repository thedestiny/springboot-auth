package com.platform.contract.exception;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

@Getter
public enum ApiCodeEnum {

    SUCCESS("1000", "成功"),
    FAIL("1001", "服务异常"),
    ERROR_PARAM("1003", "参数异常"),
    ERROR_BIZ("1004", "业务异常"),

    ;

    private String code;
    private String msg;

    public static ApiCodeEnum instanceOf(String code) {
        for (ApiCodeEnum item : values()) {
            if (StrUtil.equals(item.getCode(), code)) {
                return item;
            }
        }

        return null;
    }

    ApiCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
