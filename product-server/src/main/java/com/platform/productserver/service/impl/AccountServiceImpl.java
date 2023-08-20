package com.platform.productserver.service.impl;

import com.platform.productserver.entity.Account;
import com.platform.productserver.mapper.AccountMapper;
import com.platform.productserver.service.AccountService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户账户表 服务实现类
 * </p>
 *
 * @author destiny
 * @since 2023-08-20
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

}
