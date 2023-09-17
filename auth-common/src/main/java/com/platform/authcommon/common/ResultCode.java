package com.platform.authcommon.common;

/**
 * 枚举状态码
 */
public enum ResultCode implements ErrorCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_EXIST(1003, "账户不存在"),
    SAVE_FAILURE(1004, "数据保存失败"),
    // SAVE_FAILURE(1005, "数据不存在"),

    ;


    private Integer code;
    private String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
