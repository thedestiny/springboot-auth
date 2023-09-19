package com.platform.productserver.mapper;

import com.platform.productserver.entity.BtransLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.productserver.entity.GiveLog;

import java.util.List;

/**
 * <p>
 * B端操作流水 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-09-17
 */
public interface BtransLogMapper extends BaseMapper<BtransLog> {


    Integer insertEntityList(List<BtransLog> logList);
}
