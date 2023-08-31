package com.platform.productserver.mapper;

import com.platform.productserver.entity.Merchant;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商户账户表 Mapper 接口
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface MerchantMapper extends BaseMapper<Merchant> {

    /**
     * 查询商户信息
     */
    Merchant queryMerchantInfo(@Param("merchantNo") String merchantNo, @Param("accountType") Integer accountType);

    /**
     * 查询商户信息 读锁
     */
    Merchant queryMerchantForUpdate(Long id);

    /**
     * 根据账户编号查询商户信息
     */
    Merchant queryMerchantByNo(String accNo);
}
