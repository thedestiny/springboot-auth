package com.platform.contract.dto.req;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class EsignConfigReq implements Serializable {

    @NotNull(message = "id不能为空")
    private Long id;

    public String toString() {
        try {
            return JSONObject.toJSONString(this);
        } catch (Exception var2) {
            return super.toString();
        }
    }
}
