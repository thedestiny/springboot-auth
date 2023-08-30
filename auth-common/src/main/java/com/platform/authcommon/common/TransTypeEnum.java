package com.platform.authcommon.common;

import com.platform.authcommon.exception.AppException;
import lombok.Getter;

@Getter
public enum TransTypeEnum {

    TRANS_IN(101, "转入", "+"),
    TRANS_OUT(102, "转出", "-"),

    FREEZE(201, "冻结", "|"),
    UNFREEZE(202, "解冻", "|"),
    FREEZE_OUT(203, "解冻并出账", "-"),


    ;


    TransTypeEnum(Integer code, String name, String opt) {
        this.code = code;
        this.name = name;
        this.opt = opt;
    }

    private Integer code;

    private String name;

    private String opt;

    /**
     * 交易交易类型
     */
    public static TransTypeEnum checkTransType(Integer transType){

        for (TransTypeEnum node : TransTypeEnum.values()){
            if(node.code == transType){
                return node;
            }
        }
        throw new AppException(ResultCode.NOT_EXIST, "交易交易类型不存在");
    }




}
