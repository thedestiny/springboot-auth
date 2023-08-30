package com.platform.productserver.web;

import com.platform.authcommon.common.Result;
import com.platform.productserver.dto.AccountDto;
import com.platform.productserver.dto.BusinessDto;
import com.platform.productserver.dto.MerchantDto;
import com.platform.productserver.dto.TradeDto;
import com.platform.productserver.service.MerchantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商户账户操作
 */
@Slf4j
@RestController
@Api(tags = "账户操作")
@RequestMapping(value = "api/merchant")
public class MerchantController {

    @Autowired
    private MerchantService merchantService;

    @ApiOperation("商户账户开户")
    @PostMapping(value = "open")
    public Result<Boolean> openAccount(@RequestBody MerchantDto account){
        boolean result = merchantService.openAccount(account);
        return Result.success(result);
    }

    @ApiOperation("商户账户交易")
    @PostMapping(value = "trade")
    public Result<Boolean> trade(@RequestBody BusinessDto trade){
        boolean result = merchantService.trade(trade);
        return Result.success(result);
    }
}
