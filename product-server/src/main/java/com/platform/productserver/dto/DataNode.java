package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-12-14 3:42 下午
 */

@Data
public class DataNode implements Serializable {

    private String userId;

    private String articleId;

    private String time;

}
