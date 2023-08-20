package com.platform.productserver.redpkg;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReceivePkgReq implements Serializable {

    private static final long serialVersionUID = 4699754861708303761L;

    // 红包单号
    private String orderNo;
    // 红包请求号
    private String requestNo;
    // 领红包人id 红包类型 1-个人红包 2-群红包平分模式 2群红包拼手气
    private String receiverId;
    // 来源
    private String source;
    // 应用id
    private String appId;
    // 产品类型
    private String prodType;
    // 备注
    private String remark;


}
