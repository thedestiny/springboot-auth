package com.platform.contract.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TemplateConfDto implements Serializable {

    private Long id;
    private String storagePath;
    private String name;
    private String userId;
    private String esignFileId;
    private String docTemplateId;
    private String contractConfig;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;

    public String toString() {
        try {
            return JSONObject.toJSONString(this);
        } catch (Exception var2) {
            return super.toString();
        }
    }

}
