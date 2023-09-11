package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.platform.authcommon.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 发放撤回表
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_give_refund_log")
public class GiveRefundLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1069326801732569538L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务系统ID
     */
    private String appId;

    /**
     * 原发放批次号
     */
    private String batchNo;

    /**
     * 原分发流水号
     */
    private String refundNo;

    /**
     * 本次请求号
     */
    private String requestNo;

    /**
     * 请求撤回金额
     */
    private BigDecimal amount;

    /**
     * 实际撤回金额
     */
    private BigDecimal refundedAmount;

    /**
     * 状态 0:处理中 1:成功 2:失败
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 备注
     */
    private String remark;

    /**
     * 撤回类型 1：撤回并销毁 2：仅撤回到账户余额
     */
    private Integer refundType;

    /**
     * 撤回账号类型 10-消费,11-外部,12-管理者
     */
    private Integer accountType;

    /**
     * 是否允许欠款 0-否 1-是
     */
    private Integer handleDebit;

    /**
     * 是否处理结算 0-否 1-是
     */
    private Integer handleSettle;

    /**
     * 是否为自动撤回 0-否 1-是
     */
    private Integer autoRefund;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 转出账户
     */
    private String transOut;

    /**
     * 转入账户
     */
    private String transIn;

    /**
     * 账单摘要
     */
    private String tradeSummary;

    /**
     * 来源
     */
    private String source;


}
