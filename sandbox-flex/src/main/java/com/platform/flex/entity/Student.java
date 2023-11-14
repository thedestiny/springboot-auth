package com.platform.flex.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * onUpdate = MyUpdateListener.class
 *
 * @Description
 * @Date 2023-11-14 2:25 PM
 */

@Data
@Table(value = "tb_student")
public class Student implements Serializable {

    private static final long serialVersionUID = 4587464894057074593L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;

    private BigDecimal weight;

    private Integer age;


    @Column(value = "create_time", onInsertValue = "now()")
    private Date createTime;

    @Column(ignore = true)
    private Date updateTime;


}
