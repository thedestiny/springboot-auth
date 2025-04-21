package com.platform.utils;

import com.platform.pojo.base.BaseResp;
import org.springframework.http.HttpStatus;

public class RespUtils {



    public static <T> BaseResp<T> success() {
        BaseResp<T> resp = new BaseResp<>();
        return resp;
    }

    public static <T> BaseResp<T> success(T data, String msg) {
        BaseResp<T> resp = new BaseResp<>();
        resp.setData(data);
        resp.setMsg(msg);
        return resp;
    }


    public static <T> BaseResp<T> success(T data) {
        BaseResp<T> resp = new BaseResp<>();
        resp.setData(data);
        return resp;
    }

    public static <T> BaseResp<T> failure() {
        BaseResp<T> resp = new BaseResp<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败");
        return resp;
    }

    public static <T> BaseResp<T> failure(String msg) {
        BaseResp<T> resp = new BaseResp<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), msg);
        return resp;
    }

    public static <T> BaseResp<T> result(Integer code, String msg) {
        BaseResp<T> resp = new BaseResp<>(code, msg);
        return resp;
    }

    public static <T> BaseResp<T> warn() {
        BaseResp<T> resp = new BaseResp<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "参数错误");
        return resp;
    }
}
