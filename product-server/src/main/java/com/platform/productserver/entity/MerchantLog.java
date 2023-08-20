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
 * 商户账户流水表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_merchant_log")
public class MerchantLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易流水id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户id
     */
    private Long accountId;

    /**
     * 商户号
     */
    private String merchantNo;

    /**
     * 账户号
     */
    private String accNo;

    /**
     * 账户类型：0-发放账户 1-结算账户 2-商户账户
     */
    private Integer accountType;

    /**
     * 请求号
     */
    private String requestNo;

    /**
     * 业务订单号
     */
    private String orderNo;

    /**
     * 对方账户id
     */
    private String otherAccount;

    /**
     * 账户类型
     */
    private String otherAccountType;

    /**
     * 交易类型：0-转出 1-转入 2-出账 3-入账 4-冲正 5-退款 6-撤回
     */
    private Integer actionType;

    /**
     * 业务类型
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
     * 备注
     */
    private String remark;

    /**
     * 来源
     */
    private String source;

    /**
     * 业务系统Id
     */
    private String appId;

    /**
     * 创建时间
     */
    private Date createTime;


}
