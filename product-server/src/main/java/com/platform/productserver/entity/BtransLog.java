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
 * B端操作流水
 * </p>
 *
 * @author destiny
 * @since 2023-09-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_btrans_log")
public class BtransLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -229695418249540626L;
    /**
     * 自增主键
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
     * 出账账户
     */
    private String accNo;

    /**
     * 操作类型
     */
    private Integer actionType;

    /**
     * 业务类型
     */
    private String prodType;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 状态: 0-处理中、1-成功、2-失败
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
     * appId
     */
    private String appId;

    /**
     * 专用编号
     */
    private String exclusiveNo;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 对手方账户
     */
    private String otherAccNo;

    /**
     * 序号
     */
    private Long seq;




}
