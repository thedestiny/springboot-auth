package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccountDto implements Serializable {

    private static final long serialVersionUID = -6749243131059493189L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 账户类型 账户类型：10-内部、11-外部、12-管理
     */
    private Integer accountType;





}
