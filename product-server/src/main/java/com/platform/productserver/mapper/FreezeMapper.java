package com.platform.productserver.mapper;

import com.platform.productserver.entity.Freeze;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 专款冻结表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface FreezeMapper extends BaseMapper<Freeze> {


    /**
     * 根据账户id 和 冻结类型查询冻结账户
     */
    Freeze queryFreezeAccount(@Param("accountId") Long id, @Param("freezeType") String activityType);

}
