package com.platform.productserver.service;

import com.platform.productserver.dto.BusinessDto;
import com.platform.productserver.dto.MerchantDto;
import com.platform.productserver.entity.Merchant;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 商户账户表 服务类
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
}
