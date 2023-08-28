package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.platform.authcommon.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 红包订单表
 * </p>
 *
 * @author destiny
 * @since 2023-08-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_pkg_out_log")
public class PkgOutLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2362586341888330623L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 来源
     */
    private String source;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 红包订单号
     */
    private String orderNo;

    /**
     * 产品类型：个人红包-100 群红包-101
     */
    private String prodType;

    /**
     * 发红包用户ID
     */
    private String userId;

    /**
     * 红包金额
     */
    private BigDecimal amount;

    /**
     * 已经领取金额
     */
    private BigDecimal receiveAmount;

    /**
     * 红包退回金额
     */
    private BigDecimal refundAmount;

    /**
     * 订单状态：0-处理中、1-成功、2-失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 红包到期时间
     */
    private Date expireTime;

    /**
     * 完成标识 0-未完成  1-已完成
     */
    private Integer flag;

    /**
     * 备注
     */
    private String remark;


}
