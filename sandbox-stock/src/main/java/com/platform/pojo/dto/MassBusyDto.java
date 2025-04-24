package com.platform.pojo.dto;

import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-23 3:33 PM
 */

@Data
public class MassBusyDto {

    // 已约日期 2024-12-01,当前时间之后的7天内预约情况
    private String date;

    // 已约时间 09:30
    private List<TimeNode> timeList;


}
