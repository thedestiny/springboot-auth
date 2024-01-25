package com.platform.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-01-25 10:51 AM
 */

@Data
public class Result<T> implements Serializable {


    private Integer code;

    private String msg;

    private T data;

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("成功");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> failure(String msg) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg(msg);
        return result;
    }

    public static <T> Result<T> failure() {
        return failure("失败");
    }
}
