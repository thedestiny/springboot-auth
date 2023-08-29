package com.platform.productserver.mapper;

import com.platform.productserver.entity.PkgOutLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.productserver.ext.PkgOutLogExt;

import java.util.List;

/**
 * <p>
 * 红包订单表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-08-16
 */
public interface PkgOutLogMapper extends BaseMapper<PkgOutLog> {

    PkgOutLog selectByOrderNo(String orderNo);

    /**
     * 查询超时的红包, 红包已过期 且 状态为成功的数据
     * @return
     */
    List<PkgOutLog> queryPkgOutTimeList(PkgOutLogExt ext);
}
