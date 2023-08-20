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
 * 商户账户表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_merchant")
public class Merchant implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商户账户id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户号
     */
    private String accNo;

    /**
     * 来源
     */
    private String source;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;

    /**
     * 账户类型: 20-发放 21-结算 22-商户账户
     */
    private Integer accountType;

    /**
     * 产品类型
     */
    private String prodType;

    /**
     * 账户余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 累计入账
     */
    private BigDecimal incomeAmount;

    /**
     * 累计支出
     */
    private BigDecimal expenseAmount;

    /**
     * 累计申请
     */
    private BigDecimal applyAmount;

    /**
     * 累计冲正
     */
    private BigDecimal reversalAmount;

    /**
     * 累计撤回
     */
    private BigDecimal backAmount;

    /**
     * 累计结算
     */
    private BigDecimal settleAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 序号
     */
    private Long seq;


}
