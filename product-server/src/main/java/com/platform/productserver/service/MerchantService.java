package com.platform.productserver.service;

import com.platform.productserver.dto.BusinessDto;
import com.platform.productserver.dto.FreezeDto;
import com.platform.productserver.dto.MerchantDto;
import com.platform.productserver.entity.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * B端账户表 服务类
 * @since 2023-08-20
 */
public interface MerchantService extends IService<Merchant> {

    /**
     * 账户开户
     */
    boolean openAccount(MerchantDto account);
    /**
     * 账户交易
     */
    boolean trade(BusinessDto trade);
    /**
     * 冻结操作
     */
    boolean freeze(FreezeDto freezeDto);
    /**
     * 解冻操作
     */
    boolean unFreeze(FreezeDto freezeDto);
    /**
     * 入账并冻结
     */
    boolean freezeIn(FreezeDto freezeDto);
    /**
     * 解冻并出账
     */
    boolean unFreezeOut(FreezeDto freezeDto);
    /**
     * 批量入账
     */
    boolean batchTradeIn();
    /**
     * 批量出账
     */
    boolean batchTradeOut();
}
