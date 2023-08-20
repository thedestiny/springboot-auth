package com.platform.productserver.redpkg;

/**
 * 红包类型枚举
 */
public enum RedPkgEnum {

    SINGLE(1, 1, "单个红包", "singleRedPkg"),
    GROUP_AVERAGE(2, 2, "群红包-平分模式", "groupRedPkg"),
    GROUP_RANDOM(2, 3, "群红包-拼手气模式", "groupRedPkg");

    RedPkgEnum(Integer code, Integer type, String detail,String name) {
        this.code = code;
        this.type = type;
        this.detail = detail;
        this.name = name;
    }

    public Integer code;
    public Integer type;
    public String detail;
    public String name;


    public static RedPkgEnum queryPkgByType(Integer typ){
        for (RedPkgEnum pkg : RedPkgEnum.values()){
            if(pkg.type == typ){
                return pkg;
            }
        }
        return null;
    }

}
