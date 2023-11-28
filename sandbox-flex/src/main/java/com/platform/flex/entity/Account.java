package com.platform.flex.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Description
 * @Date 2023-11-24 3:10 PM
 */

@Data
@Table(value = "tb_account")
public class Account implements Serializable {

    private static final long serialVersionUID = -8770283690962357737L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private String userId;
    private BigDecimal balance;
    private Integer status;



}
