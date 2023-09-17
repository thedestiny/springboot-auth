package com.platform.authcommon.common;

import lombok.Getter;

/**
 * @Description 订单状态
 * @Date 2023-09-08 4:21 PM
 */

@Getter
public enum  OrderStatusEnum {

    PROCESSING(0, "PROCESSING", "处理中"),
    SUCCESS(1, "SUCCESS", "成功"),
    FAIL(2, "FAIL", "失败"),
    CLOSE(3, "CLOSED", "关闭");


    private final Integer code;
    private final String msg;
    private final String detail;




    OrderStatusEnum(int status, String showCode, String detail) {
        this.code = status;
        this.msg = showCode;
        this.detail = detail;
    }

    public static String getShowCodeByStatus(Integer status) {
        for(OrderStatusEnum node : OrderStatusEnum.values()){
            if (node.code.equals(status)){
                return node.msg;
            }
        }
        return null;
    }

}
