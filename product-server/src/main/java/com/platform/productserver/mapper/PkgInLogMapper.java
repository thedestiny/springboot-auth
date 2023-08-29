package com.platform.productserver.mapper;

import com.platform.productserver.entity.PkgInLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 红包领取记录表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-08-16
 */
public interface PkgInLogMapper extends BaseMapper<PkgInLog> {

    /**
     * 查询红包领取记录
     */
    List<PkgInLog> queryPkgInLogList(PkgInLog log);

}
