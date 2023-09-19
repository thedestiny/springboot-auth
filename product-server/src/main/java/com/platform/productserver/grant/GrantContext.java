package com.platform.productserver.grant;

import com.platform.productserver.entity.BtransLog;
import com.platform.productserver.entity.CtransLog;
import com.platform.productserver.entity.GiveBatchInfo;
import com.platform.productserver.entity.GiveLog;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Description 积分分发上下文
 * @Date 2023-09-18 5:01 PM
 */
@Data
public class GrantContext implements Serializable {

    private static final long serialVersionUID = -6215186770229376280L;

    // 数据保存状态
    private boolean saveFlag;

    private BatchGiveReq batchReq;

    // 批次信息
    private GiveBatchInfo batchInf;

    private List<GiveLog> logs;

    private List<GiveUserDto> userList;

    private List<CtransLog> ctransLogs;

    private List<BtransLog> btransLogs;



}
