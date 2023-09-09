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
 * 专款冻结表
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("tb_freeze")
public class Freeze extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 5411920656490667304L;

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



}
