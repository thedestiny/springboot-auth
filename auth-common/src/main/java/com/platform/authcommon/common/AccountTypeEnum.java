package com.platform.authcommon.common;

import com.platform.authcommon.exception.AppException;
import lombok.Getter;

/**
 * 账户类型枚举
 * 10-内部、11-外部、12-管理
 */

@Getter
public enum AccountTypeEnum {

    // 10-内部账户 11-外部账户 12-管理者
    INNER(10,"内部账户", "I"),
    OUTER(11,"外部账户", "O"),
    MANAGE(12,"管理账户", "M"),

    // 30-发放账户 31-商户账户 32-结算账户
    GRANT(30,"发放账户", "G"),
    BUSINESS(31,"商户账户", "B"),
    SETTLE(32,"结算账户", "S"),
    ;

    AccountTypeEnum(Integer code, String name, String prefix) {
        this.code = code;
        this.name = name;
        this.prefix = prefix;
    }

    private Integer code;
    private String name;
    private String prefix;


    /**
     * 校验账户类型
     */
    public static AccountTypeEnum checkAccountType(Integer accountType){

        for (AccountTypeEnum node : AccountTypeEnum.values()){
            if(node.code == accountType){
                return node;
            }
        }
        throw new AppException(ResultCode.NOT_EXIST, "账户类型不存在");
    }


}
