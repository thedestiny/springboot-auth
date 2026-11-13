package com.platform.contract.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 合同实体
 */
@Data
@TableName("t_contract")
@Schema(description = "合同表")
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "合同模板ID")
    private Long templateId;

    @Schema(description = "甲方名称")
    private String partyAName;

    @Schema(description = "甲方类型")
    private String partyAType;

    @Schema(description = "甲方证件号/用户ID")
    private String partyAId;

    @Schema(description = "乙方名称")
    private String partyBName;

    @Schema(description = "乙方类型")
    private String partyBType;

    @Schema(description = "乙方证件号/用户ID")
    private String partyBId;

    @Schema(description = "合同状态")
    private String status;

    @Schema(description = "合同描述")
    private String description;

    @Schema(description = "e签宝签署流程ID")
    private String esignFlowId;

    @Schema(description = "e签宝文件ID")
    private String esignFileId;

    @Schema(description = "e签宝文件地址")
    private String esignFileUrl;

    @Schema(description = "合同编号")
    private String contractNo;

    @Schema(description = "合同名称")
    private String contractName;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "更新时间")
    private Date updateTime;

    public String toString() {
        try {
            return JSONObject.toJSONString(this);
        } catch (Exception var2) {
            return super.toString();
        }
    }
}
