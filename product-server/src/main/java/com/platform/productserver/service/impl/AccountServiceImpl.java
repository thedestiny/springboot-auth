package com.platform.productserver.service.impl;

import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.entity.Account;
import com.platform.productserver.mapper.AccountLogMapper;
import com.platform.productserver.mapper.AccountMapper;
import com.platform.productserver.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 用户账户表 服务实现类
 * @since 2023-08-20
 */

@Slf4j
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountLogMapper logMapper;
    @Autowired
    private TransactionTemplate template;


    @Override
    public boolean trade(TradeDto tradeDto) {
        return false;
    }
}
