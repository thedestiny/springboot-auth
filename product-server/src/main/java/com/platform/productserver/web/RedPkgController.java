package com.platform.productserver.web;

import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.common.Result;
import com.platform.productserver.business.RedPkgBusiness;
import com.platform.productserver.redpkg.ReceivePkgReq;
import com.platform.productserver.redpkg.SendPkgReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 红包业务控制器
 * @Date 2023-08-17 9:41 AM
 */

@Slf4j
@RestController
@RequestMapping(value = "api/redpkg")
public class RedPkgController {

    @Autowired
    private RedPkgBusiness pkgBusiness;

    @PostMapping(value = "/send")
    public Result<Boolean> sendRedPkg(@RequestBody SendPkgReq sendPkgReq){
        log.info("发送红包参数 {}", JSONObject.toJSONString(sendPkgReq));
        boolean result = pkgBusiness.sendRedPkg(sendPkgReq);
        return Result.success(result);
    }


    @PostMapping(value = "/receive")
    public Result<Boolean> receivePkg(@RequestBody ReceivePkgReq pkgReq){
        log.info("领取红包参数 {}", JSONObject.toJSONString(pkgReq));
        boolean result = pkgBusiness.receiveRedPkg(pkgReq);
        return Result.success(result);
    }






}
