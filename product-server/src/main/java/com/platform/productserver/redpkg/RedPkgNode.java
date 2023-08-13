package com.platform.productserver.redpkg;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红包信息
 */
@Data
public class RedPkgNode implements Serializable {

    private static final long serialVersionUID = -2331279787533960846L;
    // 红包单号
    private String orderNo;
    // 红包id
    private String id;
    // 红包金额
    private BigDecimal amount;

    public RedPkgNode() {
    }

    public RedPkgNode(String orderNo, String id, BigDecimal amount) {
        this.orderNo = orderNo;
        this.id = id;
        this.amount = amount;
    }
}
