package com.platform.pojo.req;


import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 * 预约请求对象
 */
@Data
public class ReservationReq implements Serializable {

    private static final long serialVersionUID = 3280581026430789321L;

    @NotEmpty(message = "店铺id不能为空")
    private Long shopId;

    @NotEmpty(message = "预约明细不能为空")
    private List<ResDetailNode> nodeList;

    private Long userId;

    @NotEmpty(message = "项目id不能为空")
    private Long itemId;

    private Long masseurId;

    // 用户预约id列表
    private List<String> resIdList;

    // 是否新增预约 1 是 0 否,如果不是新增，会删除之前的shopId-itemId-masseurId预约信息, 默认值 1-是
    private Integer addFlag;



}
