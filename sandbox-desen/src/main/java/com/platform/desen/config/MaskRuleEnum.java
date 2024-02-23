package com.platform.desen.config;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-02-23 4:57 PM
 */
public enum MaskRuleEnum {

    PHONE("phone", "phoneHandler"),
    NAME("name", "nameHandler"),
    EMAIL("email", "emailHandler"),
    ID_CARD("idCard", "idCardHandler"),
    ADDRESS("address", "addressHandler"),
    BANK("bank", "bankHandler"),

    ;

    MaskRuleEnum(String rule, String handler) {
        this.rule = rule;
        this.handler = handler;
    }

    public String rule;

    public String handler;

    public final static Map<String, String> map = new HashMap<String, String>();

    static {

        for (MaskRuleEnum rule : MaskRuleEnum.values()) {
            map.put(rule.rule, rule.handler);
        }
    }


    public static String match(String name) {
        return map.get(name);

    }
}
