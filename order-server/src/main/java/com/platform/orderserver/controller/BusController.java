package com.platform.orderserver.controller;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.authcommon.common.Result;
import com.platform.orderserver.dto.PayDto;
import com.platform.orderserver.stragey.BaseBusiness;
import com.platform.orderserver.utils.PayTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(value = "api/bus")
public class BusController {

    // alipayAppBusiness   ->  AlipayAppBusiness
    // unionPayAppBusiness ->  UnionPayAppBusiness
    // weixinAppBusiness   ->  WeixinAppBusiness
    @Autowired
    private Map<String, BaseBusiness> businessMap;

    @PostMapping(value = "handle")
    public Result<String> handle(@RequestBody PayDto pay) {
        // 支付宝支付
        if (pay.getPayType() == 1) {
            BaseBusiness business = businessMap.get("alipayAppBusiness");
            return business.handleOrderFlow(pay);
        }
        // 匹配业务处理器
        BaseBusiness business = matchBusiness(pay.getPayType());
        Result<String> result = business.handleOrderFlow(pay);
        log.info("result is {}", JSONObject.toJSONString(result));
        return result;
    }
    /**
     * 匹配业务处理器
     */
    public BaseBusiness matchBusiness(Integer code) {
        PayTypeEnum payTypeEnum = PayTypeEnum.businessMap.get(code);
        if (payTypeEnum == null) {
            throw new RuntimeException("业务类型未找到");
        }
        BaseBusiness bean = SpringUtil.getBean(payTypeEnum.getKlass());
        if (bean == null) {
            throw new RuntimeException("业务类型未找到");
        }
        return bean;
    }


}
