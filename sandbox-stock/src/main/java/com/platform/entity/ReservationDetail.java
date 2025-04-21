package com.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户预约明细表
 */
@Data
@TableName("tb_reservation_detail")
public class ReservationDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    // 预约明细id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 预约号
    private String resId;

    // 技师id
    private Long masseurId;

    // 预约日期
    private String reservationDate;

    // 预约时间
    private String reservationTime;

    private Long seq;

    // 创建时间
    private Date createTime;

    // 更新时间
    private Date updateTime;

    // 备注
    private String remark;


}
