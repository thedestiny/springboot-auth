package com.platform.productserver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.platform.authcommon.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 积分分发信息表
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_give_batch_info")
public class GiveBatchInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 2404274366281779756L;
    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 业务系统ID
     */
    private String appId;

    /**
     * 分发批次号
     */
    private String batchNo;

    /**
     * 活动类型
     */
    private String activityType;

    /**
     * 商户编号
     */
    private String merchantNo;

    /**
     * 费控单编号
     */
    private String serialNo;

    /**
     * 总分发人数
     */
    private Integer giveCount;

    /**
     * 已接收人数
     */
    private Integer receiveCount;

    /**
     * 分发结果类型 0:整体成功 1:部分成功
     */
    private Integer resultType;

    /**
     * 信息状态 0:处理中 1:处理完成
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 分发总数量
     */
    private BigDecimal amount;

    /**
     * 分发渠道
     */
    private String channel;

    /**
     * 活动编号
     */
    private String activityNo;

    /**
     * 转出账号
     */
    private String outAccNo;

    /**
     * 来源
     */
    private String source;

    /**
     * 已处理条数
     */
    private Integer dealCount;

    /**
     * 分发方式 0:同步 1:异步
     */
    private Integer asynFlag;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 回调方身份标志
     */
    private String callbackIdentity;

    /**
     * 珑珠有效期
     */
    private String expireTime;


}
