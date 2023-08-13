package com.platform.productserver.redpkg;

/**
 * 红包类型枚举
 */
public enum RedPkgEnum {

    SINGLE(1,1, "单个红包" ),
    GROUP_AVERAGE(2,2, "群红包-平分模式" ),
    GROUP_RANDOM(2,3, "群红包-拼手气模式" );

    RedPkgEnum(Integer code, Integer type, String detail) {
        this.code = code;
        this.type = type;
        this.detail = detail;
    }

    public Integer code;
    public Integer type;
    public String detail;


}
