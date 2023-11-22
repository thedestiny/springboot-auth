package com.platform.flex.entity;

import com.mybatisflex.annotation.*;
import com.mybatisflex.core.mask.Masks;
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

    // 数据字段脱敏
    @ColumnMask(Masks.CHINESE_NAME)
    private String username;

    private BigDecimal weight;

    private Integer age;

    @ColumnMask(Masks.MOBILE)
    private String phone;

    @ColumnMask(Masks.ID_CARD_NUMBER)
    private String idCard;

    private String address;

    private Date birthday;

    @Column(value = "create_time", onInsertValue = "now()")
    private Date createTime;
    // ignore = true,
    @Column(onUpdateValue = "now()", onInsertValue = "now()")
    private Date updateTime;


}
