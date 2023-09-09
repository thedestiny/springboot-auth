package com.platform.authcommon.common;

/**
 * @Description 账户状态
 * @Date 2023-09-08 4:19 PM
 */
public enum StatusEnum {


    DISABLE(0, "disable", "禁用"),
    ENABLE(1, "enable", "启用"),
    LOCKED(2, "locked", "锁定"),
    ;


    public Integer code;
    public String msg;
    public String detail;

    StatusEnum(Integer status, String msg, String detail) {
        this.code = status;
        this.msg = msg;
        this.detail = detail;
    }


}
