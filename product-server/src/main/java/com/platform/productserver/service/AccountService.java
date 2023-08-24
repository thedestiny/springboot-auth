package com.platform.productserver.service;

import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.Account;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户账户表 服务类
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
public interface AccountService extends IService<Account> {

    /**
     * 账户出账
     */
    boolean trade(TradeDto tradeDto);


}
