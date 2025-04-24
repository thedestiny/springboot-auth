package com.platform.pojo.res;

import com.platform.pojo.dto.MassBusyDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-23 3:30 PM
 */

@Data
public class MasseurDetailRes {

    @ApiModelProperty(value = "家政服务人员id")
    private Long id;

    @ApiModelProperty(value = "服务人员头像")
    private String avatar;

    @ApiModelProperty(value = "服务人员昵称")
    private String nickName;

    @ApiModelProperty(value = "服务人员名称")
    private String realName;

    @ApiModelProperty(value = "服务人员关联项目信息")
    private List<ItemInfoRes> itemList;

    @ApiModelProperty(value = "已约时间段,当前时间之后7天内的记录")
    private List<MassBusyDto> busyList;

}
