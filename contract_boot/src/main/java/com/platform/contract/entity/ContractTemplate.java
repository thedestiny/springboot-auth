package com.platform.contract.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("t_contract_template")
@Schema(description = "合同模板表")
public class ContractTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "模板存储路径")
    private String storagePath;

    @Schema(description = "模板名称")
    private String name;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "e签宝文件ID")
    private String esignFileId;

    @Schema(description = "e签宝文档模板ID")
    private String docTemplateId;

    @Schema(description = "合同模板配置(JSON)")
    private String contractConfig;

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
