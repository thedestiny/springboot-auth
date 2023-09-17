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
 * C端操作流水
 * </p>
 *
 * @author destiny
 * @since 2023-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_ctrans_log")
public class CtransLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -3838573716147997589L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 来源
     */
    private String source;

    /**
     * 记录Id
     */
    private Long fid;

    /**
     * 订单明细号
     */
    private String requestNo;

    /**
     * userId
     */
    private String userId;

    /**
     * 账户类型 10:内部 11:外部 12:管理 31:商户
     */
    private Integer accountType;

    /**
     * 业务类型
     */
    private String prodType;

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 流水状态:0-处理中、1-成功、2-失败
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
     * 序号
     */
    private Long seq;


}
