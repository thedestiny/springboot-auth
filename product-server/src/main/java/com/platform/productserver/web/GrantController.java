package com.platform.productserver.web;

import com.platform.authcommon.common.Result;
import com.platform.productserver.grant.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Date 2023-08-30 2:29 PM
 */


@Slf4j
@Api(tags = "积分发放操作")
@RestController
@RequestMapping(value = "api/point/give")
public class GrantController {

    @Autowired
    private GrantBusiness grantBusiness;

    @ApiOperation(value = "积分分发-单笔")
    @PostMapping(value = "single")
    public Result<GiveResp> point(@RequestBody GiveReq giveReq){
        GiveResp result = grantBusiness.point(giveReq);
        return Result.success(result);
    }


    @ApiOperation(value = "积分分发-批量")
    @PostMapping(value = "batch")
    public Result<GiveResp> pointBatch(@RequestBody BatchGiveReq batchReq){
        GiveResp result = grantBusiness.pointBatch(batchReq);
        return Result.success(result);
    }

    @ApiOperation(value = "积分分发-撤回")
    @PostMapping(value = "refund")
    public Result<GiveResp> refund(@RequestBody GiveRefundReq refundReq){
        GiveResp result = grantBusiness.refund(refundReq);
        return Result.success(result);
    }
}
