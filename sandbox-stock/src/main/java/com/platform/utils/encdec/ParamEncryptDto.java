package com.platform.utils.encdec;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-04 10:53 AM
 */

@Data
public class ParamEncryptDto implements Serializable {


    private String appId;

    private String encryptData;

    private String encryptKey;

    public String toJSON(){
        return JSON.toJSONString(this);
    }


}
