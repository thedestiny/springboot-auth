package com.platform.authmpg.dto;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platform.authmpg.config.AppJsonSerializer;
import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.springframework.validation.ValidationUtils;
import sun.misc.Contended;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-06 3:45 PM
 */

@Data
public class UserDto implements Serializable {

    // 缓存行 填充
    @Contended
    private Long id;

    // 缓存行 填充
    @Contended
    private String name;

    @JsonSerialize(using = AppJsonSerializer.class)
    private Date createTime;


    public static void main(String[] args) {

        String data = "123";

        // ValidationUtils. 参数校验
        Validate.notBlank(data, "不能为空");

    }

}
