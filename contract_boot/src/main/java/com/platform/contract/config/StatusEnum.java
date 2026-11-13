package com.platform.contract.config;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

@Getter
public enum StatusEnum {

    SIGN_FLOW_STATUS_0("0", "草稿"),
    SIGN_FLOW_STATUS_1("1", "签署中"),
    SIGN_FLOW_STATUS_2("2", "完成"),
    SIGN_FLOW_STATUS_3("3", "撤销"),
    SIGN_FLOW_STATUS_5("5", "过期"),
    SIGN_FLOW_STATUS_7("7", "拒签"),


    SIGN_STATUS_0("0", "等待签署"),
    SIGN_STATUS_1("1", "签署中"),
    SIGN_STATUS_2("2", "已签署"),
    SIGN_STATUS_3("3", "等待审批"),
    SIGN_STATUS_4("4", "已拒签");

    private String code;
    private String memo;

    public static String transSignStatusName(String code) {
        if (ObjectUtils.isEmpty(code)) {
            return "";
        } else if (StringUtils.equals(code, SIGN_STATUS_0.getCode())) {
            return SIGN_STATUS_0.getMemo();
        } else if (StringUtils.equals(code, SIGN_STATUS_1.getCode())) {
            return SIGN_STATUS_1.getMemo();
        } else if (StringUtils.equals(code, SIGN_STATUS_2.getCode())) {
            return SIGN_STATUS_2.getMemo();
        } else if (StringUtils.equals(code, SIGN_STATUS_3.getCode())) {
            return SIGN_STATUS_3.getMemo();
        } else {
            return StringUtils.equals(code, SIGN_STATUS_4.getCode()) ? SIGN_STATUS_4.getMemo() : "";
        }
    }

    StatusEnum(String code, String memo) {
        this.code = code;
        this.memo = memo;
    }
}
