package com.platform.senstive;

import java.util.function.Function;


public enum SensitiveEnum {


    /**
     * 用户名
     */
    USERNAME(s -> s.replaceAll("\\S*(\\S)", "***$1")),
    /**
     * 身份证
     */
    ID_CARD(s -> s.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1****$2")),
    /**
     * 手机号
     */
    PHONE(s -> s.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2")),
    /**
     * 地址
     */
    ADDRESS(s -> s.replaceAll("(\\S{3})\\S{2}(\\S*)\\S{2}", "$1****$2****")),
    // "(\\w*)\\w{1}@(\\w+)", "$1*@$2"
    EMAIL(s -> s.replaceAll("(\\w+)\\w{4}@(\\w+)", "$1***@$2")),
    ;


    public Function<String, String> express;

    SensitiveEnum(Function<String, String> express) {
        this.express = express;
    }

    public static void main(String[] args) {

        String apply = SensitiveEnum.EMAIL.express.apply("xieyue86@163.com");
        System.out.println(apply);

    }


}
