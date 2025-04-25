package com.platform.pojo.dto;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-25 3:11 PM
 */

@Data
public class AvaiTimeDto implements Serializable {

    private static final long serialVersionUID = 7196755100845023014L;

    // 开始时间
    private Date startTime;
    // 结束时间
    private Date endTime;
    // 可以时间 单位分钟
    private Long between;

    public AvaiTimeDto(Date startTime, Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.between = DateUtil.between(startTime, endTime, DateUnit.MINUTE, true);
    }

    public boolean isBetween(Date date) {
        // 开始时间在 当前可用时间区间的开始时间
        if(ObjectUtil.compare(date, startTime) < 0 && ObjectUtil.compare(date, endTime) < 0){
            return true;
        }
        if(ObjectUtil.compare(date, startTime) > 0 && ObjectUtil.compare(date, endTime) > 0){
            return false;
        }
        if (ObjectUtil.compare(date, startTime) >= 0 && ObjectUtil.compare(date, endTime) <= 0) {
            //
            this.startTime = date;
            this.between = DateUtil.between(startTime, endTime, DateUnit.MINUTE, true);
            return true;
        } else {
            return true;
        }
    }
}
