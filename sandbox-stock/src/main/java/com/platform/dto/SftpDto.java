package com.platform.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-03-15 2:23 PM
 */

@Data
public class SftpDto implements Serializable  {


    // 日期
    private String date;

    // 文件名称
    private String file;

    // 路径
    private String path;

}
