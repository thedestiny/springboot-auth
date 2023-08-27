package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.platform.authcommon.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 用户账户表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_account")
public class Account extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.NONE)
    private Long id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 账户编号
     */
    private String accNo;

    /**
     * 账户类型：10-内部、12-外部、13-管理
     */
    private Integer accountType;

    /**
     * 用户余额
     */
    private BigDecimal balance;

    /**
     * 账户状态:0-禁用，1-启用
     */
    private Integer status;

    /**
     * 总收入
     */
    private BigDecimal incomeAmount;

    /**
     * 总支出
     */
    private BigDecimal expenseAmount;

    /**
     * 欠款金额
     */
    private BigDecimal creditAmount;

    /**
     *  userId、account_type, seq建立唯一索引
     */
    private Long seq;



}
