package com.platform.productserver.entity;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 冻结流水表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_freeze_log")
public class FreezeLog implements Serializable {

    private static final long serialVersionUID = -352077470174968860L;
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 账户ID
     */
    private Long accountId;

    /**
     * 交易类型：0-冻结 1-解冻 2-解冻出账 3-入账冻结
     */
    private Integer actionType;

    /**
     * 账户号
     */
    private String accNo;

    /**
     * 冻结类型
     */
    private String freezeType;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 请求号
     */
    private String requestNo;

    /**
     * 业务订单号
     */
    private String orderNo;

    /**
     * 业务类型
     */
    private String prodType;

    /**
     * 业务系统ID
     */
    private String appId;

    /**
     * 来源
     */
    private String source;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;


}
