package com.platform.productserver.mapper;

import com.platform.productserver.entity.Account;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 用户账户表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface AccountMapper extends BaseMapper<Account> {


    /**
     * 查询账户信息
     * @param userId
     * @param accountType
     * @return
     */
    Account queryAccount(@Param("userId") String userId, @Param("accountType") Integer accountType);

    /**
     * 根据账户查询
     * @param accountId
     * @return
     */
    Account queryAccountForUpdate(Long accountId);


}
