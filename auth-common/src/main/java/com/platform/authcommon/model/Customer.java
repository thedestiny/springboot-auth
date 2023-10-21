package com.platform.authcommon.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer implements Recommend {

    // 数据id
    private Long id;
    // 用户id
    private Long customerId;
    // 商品id
    private Long commodityId;
    // 分值
    private Float value;

    // private Date createTime;

    @Override
    public long getUserId() {
        return this.customerId;
    }

    @Override
    public long getItemId() {
        return this.commodityId;
    }
    @Override
    public float getValue() {
        return this.value;
    }
}

