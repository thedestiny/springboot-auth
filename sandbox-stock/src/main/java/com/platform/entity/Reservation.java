package com.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 用户预约表
 * </p>
 *
 * @author kaiyang
 * @since 2024-12-01
 */
@Data
@TableName("tb_reservation")
public class Reservation  implements Serializable {

    private static final long serialVersionUID = -2061890631886386745L;

    // 预约id
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // 预约id
    private String resId;

    // 订单号
    private Long orderId;

    // 项目id
    private Long itemId;

    // 技师id
    private Long masseurId;

    // 店铺id
    private Long shopId;

    // 用户id
    private Long userId;

    // 预约开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    // 预约结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    // 预约创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;






}
