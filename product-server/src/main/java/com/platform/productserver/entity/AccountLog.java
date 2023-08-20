package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 账户流水表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_account_log")
public class AccountLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 流水号
     */
    private String requestNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 账户编号
     */
    private String accNo;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 账户类型: 1-内部 2-外部 3-超管
     */
    private Integer accountType;

    /**
     * 对方账户id
     */
    private String otherAccount;

    /**
     * 对方账户类型
     */
    private Integer otherAccountType;

    /**
     * 交易类型: 1-转入 2-转出 3-消费 4-充值 5-退款 6-冲正 7-发放撤回 8-借款 9-还款
     */
    private Integer actionType;

    /**
     * 业务类型: 红包-001 转账-002
     */
    private String prodType;

    /**
     * 交易金额
     */
    private BigDecimal transAmount;

    /**
     * 交易后余额
     */
    private BigDecimal balance;

    /**
     * 数据来源
     */
    private String source;

    /**
     * app_id
     */
    private String appId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 流水号
     */
    private Long seq;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
