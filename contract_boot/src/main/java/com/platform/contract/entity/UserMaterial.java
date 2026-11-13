package com.platform.contract.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_user_material")
@Schema(description = "用户材料表")
public class UserMaterial implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "账户类型")
    private String accountType;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "机构名称")
    private String orgName;

    @Schema(description = "机构证件号")
    private String orgIdCardNum;

    @Schema(description = "法定代表人姓名")
    private String legalRepName;

    @Schema(description = "法定代表人证件号")
    private String legalRepIdCardNum;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

    @TableField(exist = false)
    @Schema(description = "机构ID")
    private String orgId;

    public String toString() {
        try {
            return JSONObject.toJSONString(this);
        } catch (Exception var2) {
            return super.toString();
        }
    }
}
