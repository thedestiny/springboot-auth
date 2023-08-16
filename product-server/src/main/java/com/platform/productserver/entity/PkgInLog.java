package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 红包领取记录表
 * </p>
 *
 * @author destiny
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_pkg_in_log")
public class PkgInLog implements Serializable {

    private static final long serialVersionUID = 4975972017839942843L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 红包发放记录ID
     */
    private Long fid;

    /**
     * 来源
     */
    private String source;

    /**
     * 请求号
     */
    private String requestNo;

    /**
     * 红包订单号
     */
    private String orderNo;

    /**
     * 领红包用户ID
     */
    private String userId;

    /**
     * 领取金额
     */
    private BigDecimal amount;

    /**
     * 订单状态: 0- 处理中、1-成功、 2-失败
     */
    private Integer status;

    /**
     * 异常原因
     */
    private String errorMsg;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 产品类型：个人红包-100 群红包-101
     */
    private String prodType;

    /**
     * 操作：0-领取  1-退回
     */
    private Integer actionType;

    /**
     * 备注
     */
    private String remark;


}
