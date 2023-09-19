package com.platform.productserver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.productserver.entity.GiveLog;

import java.util.List;

/**
 * <p>
 * 积分分发订单表 服务类
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
public interface GiveLogService extends IService<GiveLog> {

    /**
     * 保存分发日志信息
     */
    Integer insertGiveLogList(List<GiveLog> logList);





}
