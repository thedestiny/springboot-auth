package com.platform.productserver.mapper;

import com.platform.productserver.entity.CtransLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * C端操作流水 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-09-17
 */
public interface CtransLogMapper extends BaseMapper<CtransLog> {


    Integer insertEntityList(List<CtransLog> logList);

    /**
     * 根据 fid list 查询数据
     */
    List<CtransLog> selectLogListByFid(List<Long> idList);
}
