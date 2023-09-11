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
 * 积分分发订单表
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_give_log")
public class GiveLog extends BaseEntity implements Serializable {

    private static final long serialVersionUID = -4681745623756539562L;
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 请求号
     */
    private String requestNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 分发金额
     */
    private BigDecimal amount;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 分发账号
     */
    private String outAccNo;

    /**
     * 目标账户
     */
    private String inAccNo;

    /**
     * 龙民ID
     */
    private String userId;

    /**
     * 账户类型 10:内部 11:外部 12:管理 31:商户
     */
    private Integer accountType;

    /**
     * 数据类型 1-toC 2-toB
     */
    private Integer dataType;

    /**
     * 分发方式 0-同步; 1-异步
     */
    private Integer giveType;

    /**
     * 状态 0:处理中 1:成功 2:失败
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 失败原因
     */
    private String failureMsg;

    /**
     * 活动编号
     */
    private String activityNo;

    /**
     * 业务类型编号
     */
    private String prodType;

    /**
     * 分发摘要
     */
    private String tradeSummary;

    /**
     * 专款专用编号
     */
    private String exclusiveNo;

    /**
     * 渠道信息
     */
    private String channel;

    /**
     * app id
     */
    private String appId;

    /**
     * 来源
     */
    private String source;


}
