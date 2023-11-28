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
    // 用户姓名 数据字段脱敏
    @ColumnMask(Masks.CHINESE_NAME)
    private String username;
    // 体重
    private BigDecimal weight;
    // 年龄
    private Integer age;
    // 手机号
    @ColumnMask(Masks.MOBILE)
    private String phone;
    // 身份证号
    @ColumnMask(Masks.ID_CARD_NUMBER)
    private String idCard;
    // 地址
    private String address;
    // 生日
    private Date birthday;
    // 创建时间和修改时间
    @Column(value = "create_time", onInsertValue = "now()")
    private Date createTime;
    // ignore = true,
    @Column(onUpdateValue = "now()", onInsertValue = "now()")
    private Date updateTime;



}
