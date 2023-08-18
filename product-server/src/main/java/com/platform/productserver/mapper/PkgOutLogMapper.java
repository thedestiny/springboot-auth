package com.platform.productserver.mapper;

import com.platform.productserver.entity.PkgOutLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
}
