package com.platform.productserver.service;

import com.platform.productserver.dto.AccountDto;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * C端账户 服务
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface AccountService extends IService<Account> {


    /**
     * 账户开户
     */
    boolean openAccount(AccountDto account);

    /**
     * 账户交易操作
     */
    boolean trade(TradeDto tradeDto);


}
