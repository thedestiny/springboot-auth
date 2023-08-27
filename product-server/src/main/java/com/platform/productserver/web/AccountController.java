package com.platform.productserver.web;

import com.platform.authcommon.common.Result;
import com.platform.productserver.dto.AccountDto;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.service.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户操作
 */
@Slf4j
@RestController
@Api(tags = "账户操作")
@RequestMapping(value = "api/account")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation("账户开户")
    @PostMapping(value = "open")
    public Result<Boolean> openAccount(@RequestBody AccountDto account){
        boolean result = accountService.openAccount(account);
        return Result.success(result);
    }

    @ApiOperation("账户交易")
    @PostMapping(value = "trade")
    public Result<Boolean> trade(@RequestBody TradeDto trade){
        boolean result = accountService.trade(trade);
        return Result.success(result);
    }
}
