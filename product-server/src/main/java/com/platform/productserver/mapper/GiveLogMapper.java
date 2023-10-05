package com.platform.productserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.productserver.entity.GiveLog;

import java.util.List;

/**
 * <p>
 * 积分分发订单表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-09-11
 */
public interface GiveLogMapper extends BaseMapper<GiveLog> {

    /**
     * 批量保存日志信息
     * @param logList
     * @return
     */
    Integer insertEntityList(List<GiveLog> logList);


    GiveLog selectByGiveNo(String giveNo);
}
