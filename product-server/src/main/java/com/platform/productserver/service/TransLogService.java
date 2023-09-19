package com.platform.productserver.service;

import com.platform.productserver.entity.BtransLog;
import com.platform.productserver.entity.CtransLog;

import java.util.List;

/**
 * @Description
 * @Date 2023-09-18 3:27 PM
 */
public interface TransLogService {


    Integer insertCtransLogs(List<CtransLog> logList);


    Integer insertBtransLogs(List<BtransLog> logList);


}
