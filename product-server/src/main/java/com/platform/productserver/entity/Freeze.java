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
 * 专款冻结表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_freeze")
public class Freeze implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 冻结类型：2-转账冻结、3-结算冻结
     */
    private String freezeType;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


}
