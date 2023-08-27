package com.platform.productserver.redpkg;

import cn.hutool.core.util.StrUtil;

/**
 * 红包类型枚举
 */
public enum RedPkgEnum {

    SINGLE("100", 1, "单个红包", "singleRedPkg"),
    GROUP_AVERAGE("101", 2, "群红包-平分模式", "groupRedPkg"),
    GROUP_RANDOM("101", 3, "群红包-拼手气模式", "groupRedPkg");

    RedPkgEnum(String code, Integer type, String detail,String name) {
        this.code = code;
        this.type = type;
        this.detail = detail;
        this.name = name;
    }

    public String code;
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

    public static RedPkgEnum queryPkgByCode(String code){
        for (RedPkgEnum pkg : RedPkgEnum.values()){
            if(StrUtil.equals(pkg.code, code)){
                return pkg;
            }
        }
        return null;
    }
}
