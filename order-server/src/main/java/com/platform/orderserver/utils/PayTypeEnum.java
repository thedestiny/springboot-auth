package com.platform.orderserver.utils;

import com.platform.orderserver.stragey.AlipayAppBusiness;
import com.platform.orderserver.stragey.BaseBusiness;
import com.platform.orderserver.stragey.UnionPayAppBusiness;
import com.platform.orderserver.stragey.WeixinAppBusiness;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PayTypeEnum {

    ALIPAY(1,"alipayAppBusiness", "支付宝支付", AlipayAppBusiness.class),
    WEIXIN(2,"weixinAppBusiness", "微信支付", WeixinAppBusiness.class),
    UNIPAY(3,"unionPayAppBusiness", "银联支付", UnionPayAppBusiness.class),
    ;

    PayTypeEnum(Integer code, String detail, String msg, Class<? extends BaseBusiness> klass) {
        this.code = code;
        this.detail = detail;
        this.msg = msg;
        this.klass = klass;
    }

    private Integer code;
    private String detail;
    private String msg;
    private Class<? extends BaseBusiness> klass;

    public static Map<Integer, PayTypeEnum> businessMap = new HashMap<>();
    static {
        for (PayTypeEnum node : PayTypeEnum.values()){
            businessMap.put(node.getCode(), node);
        }
    }

}
