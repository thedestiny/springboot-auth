package com.platform.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ItemInfo implements Serializable {

    private static final long serialVersionUID = 5635589870012761613L;

    /** 推拿项目id */
    private Long id;

    /** 项目名称 */
    private String itemName;

    /** 项目时长,单位为分钟 */
    private Long costTime;

    /** 简介 */
    private String brief;


    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新者
     */
    @TableField(exist = false)
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 备注
     */
    private String remark;
}
