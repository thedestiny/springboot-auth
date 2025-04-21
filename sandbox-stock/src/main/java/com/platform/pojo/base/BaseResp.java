package com.platform.pojo.base;


import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class BaseResp<T> {

    private Integer code;

    private String msg;

    private T data;

    public BaseResp() {
        this.code = HttpStatus.OK.value();
        this.msg = "请求成功";
    }

    public BaseResp(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public BaseResp(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
