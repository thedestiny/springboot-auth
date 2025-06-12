package com.platform.authmpg.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.platform.authmpg.config.AppJsonSerializer;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-06 3:45 PM
 */

@Data
public class UserDto implements Serializable {


    private Long id;

    private String name;

    @JsonSerialize(using = AppJsonSerializer.class)
    private Date createTime;

}
