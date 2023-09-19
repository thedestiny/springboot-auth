package com.platform.productserver.service;

import com.platform.productserver.dto.*;
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
    boolean freezeIn(FreezeTradeDto freezeDto);
    /**
     * 解冻并出账
     */
    boolean unFreezeOut(FreezeTradeDto freezeDto);
    /**
     * 批量入账
     */
    boolean batchTradeIn(BatchTradeDto tradeDto);
    /**
     * 批量出账
     */
    boolean batchTradeOut(BatchTradeDto tradeDto);
}
