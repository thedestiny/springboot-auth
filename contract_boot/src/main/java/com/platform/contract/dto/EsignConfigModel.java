package com.platform.contract.dto;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class EsignConfigModel {

    public String createUrl;
    public String editUrl;
    public String docTemplateId;
    public Boolean alreadyConfigured;

    public String toString() {
        try {
            return JSONObject.toJSONString(this);
        } catch (Exception var2) {
            return super.toString();
        }
    }

}
