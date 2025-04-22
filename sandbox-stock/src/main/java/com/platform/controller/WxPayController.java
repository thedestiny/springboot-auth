package com.platform.controller;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.platform.config.WeixinConfig;
import com.platform.dto.PayDto;
import com.platform.pojo.dto.BaseInfoDto;
import com.platform.utils.IdGenUtils;
import com.platform.utils.ZxingUtils;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.Payer;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayRequest;
import com.wechat.pay.java.service.payments.jsapi.model.SceneInfo;
import com.wechat.pay.java.service.refund.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.jsapi.model.PrepayResponse;
import com.wechat.pay.java.service.refund.model.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-22 6:03 PM
 */

@Slf4j
@RestController
@RequestMapping(value =   "api/v1/pay")
public class WxPayController {


    @Autowired
    private WeixinConfig weixinConfig;
    @Autowired
    private JsapiService jsapiService;
    @Autowired
    private NativePayService nativePayService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private HttpServletRequest request;

    @PostMapping(value = "/order")
    public String weinxinPay(@RequestBody PayDto payDto){


        return "";
    }


    public void jsapiPay(PayDto payDto) throws Exception {

        String openId = "微信openid";

        PrepayRequest request = new PrepayRequest();
        Amount amount = new Amount();
        amount.setTotal(transYu2Fen(payDto.getAmount()));
        amount.setCurrency("CNY");
        request.setAmount(amount);
        request.setAppid(weixinConfig.getAppid());
        request.setMchid(weixinConfig.getMchId());
        request.setDescription(payDto.getTitle());
        // 添加附加参数 附加数据
        request.setAttach("order");
        // /api/v1/weixin/order/notify
        request.setNotifyUrl("https://example.com/api/v1/weixin/order/notify");
        request.setOutTradeNo("trade_no");
        Payer payer = new Payer();
        payer.setOpenid(openId);
        request.setPayer(payer);
        SceneInfo sceneInfo = new SceneInfo();
        sceneInfo.setPayerClientIp(getClientIp());
        request.setSceneInfo(sceneInfo);
        PrepayResponse prepay = jsapiService.prepay(request);
        log.info("repay is {}", JSONObject.toJSONString(prepay));
        String prepayId = prepay.getPrepayId();

        BaseInfoDto infoDto = new BaseInfoDto();
        infoDto.setPrepayId(prepayId);
        //生成签名
        Long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = RandomStringUtils.randomAlphanumeric(32);
        String sign = weixinConfig.jsApiPaySign(String.valueOf(timestamp), nonceStr, prepayId);


    }


    private String nativePay(PayDto payDto) {
        com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request = new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();
        request.setAppid(weixinConfig.getAppid());
        request.setMchid(weixinConfig.getMchId());
        request.setDescription(payDto.getTitle());
        // 添加附加参数 附加数据
        request.setAttach("order-attach");
        request.setOutTradeNo("out_trade_no");
        // 时间格式化
        request.setNotifyUrl("https://example.com/api/v1/weixin/order/notify");
        com.wechat.pay.java.service.payments.nativepay.model.Amount amount = new com.wechat.pay.java.service.payments.nativepay.model.Amount();
        amount.setCurrency("CNY");
        amount.setTotal(transYu2Fen(payDto.getAmount()));
        request.setAmount(amount);
        com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse prepay = nativePayService.prepay(request);
        String codeUrl = prepay.getCodeUrl();
        log.info("codeUrl {}", codeUrl);
        String property = System.getProperty("user.dir");
        String idStr = IdGenUtils.getIdStr();
        String path = property + "/" + idStr + ".jpg";
        ZxingUtils.createQRCodeImage("codeUrl", path);
        // 配置二维码参数
        return path;
    }



    /**
     * 转换为 分
     */
    private Integer transYu2Fen(BigDecimal amount) {
        BigDecimal mul = NumberUtil.mul(amount, 100);
        return mul.intValue();

    }



    public String getClientIp() {
        String xff = request.getHeader("X-Real-IP");
        if (xff != null) {
            return xff;
        }
        xff = request.getHeader("x-forwarded-for");
        if (xff == null) {
            return "8.8.8.8";
        }
        return xff;
    }


}
