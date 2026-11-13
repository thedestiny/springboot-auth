package com.platform.contract.dto.req;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class TemplateConfReq implements Serializable {

    @NotNull(message = "id不能为空")
    private Long id;
    private String positionPage = "3";

    private Integer partyAX = 120;
    private Integer partyAY = 100;
    private Integer partyBX = 420;
    private Integer partyBY = 100;

    public String toString() {
        try {
            return JSONObject.toJSONString(this);
        } catch (Exception var2) {
            return super.toString();
        }
    }
}
