package com.platform.contract.config;

public enum TypeEnum {

    SIGN_PARTY_A("0", "甲方"),
    SIGN_PARTY_B("1", "乙方"),

    ACCOUNT_TYPE_0("0", "个人"),
    ACCOUNT_TYPE_1("1", "公司"),

    SIGN_FLOW_UPDATE("SIGN_FLOW_UPDATE", "签署人签署完成"),
    SIGN_FLOW_FINISH("SIGN_FLOW_FINISH", "流程结束"),
    SIGN_DOC_EXPIRE_REMIND("SIGN_DOC_EXPIRE_REMIND", "流程文件过期前提醒"),
    SIGN_DOC_EXPIRE("SIGN_DOC_EXPIRE", "流程文件过期"),
    WILL_FINISH("WILL_FINISH", "意愿认证完成"),
    PARTICIPANT_MARKREAD("PARTICIPANT_MARKREAD", "签署人已读"),
    SIGN_FLOW_COMPLETE("SIGN_FLOW_COMPLETE", "流程完成"),

    ;

    private String code;
    private String memo;

    public String getCode() {
        return this.code;
    }

    public String getMemo() {
        return this.memo;
    }

    private TypeEnum(String code, String memo) {
        this.code = code;
        this.memo = memo;
    }
}
