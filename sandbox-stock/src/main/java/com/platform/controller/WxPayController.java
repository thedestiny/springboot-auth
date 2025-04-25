package com.platform.controller;

import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.platform.config.WeixinConfig;
import com.platform.dto.PayDto;
import com.platform.pojo.dto.BaseInfoDto;
import com.platform.utils.HttpUtils;
import com.platform.utils.IdGenUtils;
import com.platform.utils.ZxingUtils;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.h5.H5Service;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.jsapi.model.*;
import com.wechat.pay.java.service.payments.jsapi.model.Amount;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.payments.model.TransactionAmount;
import com.wechat.pay.java.service.refund.RefundService;
import com.wechat.pay.java.service.refund.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wechat.pay.java.service.payments.nativepay.NativePayService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders.*;
import static com.wechat.pay.contrib.apache.httpclient.constant.WechatPayHttpHeaders.WECHAT_PAY_SIGNATURE;

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
    @Lazy
    private WeixinConfig weixinConfig;
    @Autowired
    @Lazy
    @Qualifier("jsapiService")
    private JsapiService jsapiService;
    @Autowired
    @Lazy
    @Qualifier("nativePayService")
    private NativePayService nativePayService;
    @Autowired
    @Lazy
    private RefundService refundService;
    @Autowired
    private HttpServletRequest request;


    @Lazy
    @Autowired
    @Qualifier(value = "wxNotificationParser")
    private NotificationParser parser;

    @Autowired
    @Lazy
    @Qualifier(value = "h5PayService")
    private H5Service h5Service;


    /**
     * 订单支付
     */
    @PostMapping(value = "/order")
    public String wxOrderPay(@RequestBody PayDto payDto) throws Exception {
        // 支付方式很多，这里介绍 native 和 jsapiPay 两种方式
       jsapiPay(payDto);
       nativePay(payDto);

        QueryByOutRefundNoRequest req1 = new QueryByOutRefundNoRequest();
        req1.setOutRefundNo("out_refund_no");
        req1.setSubMchid("mch_id");
        Refund refund = refundService.queryByOutRefundNo(req1);
        log.info("refund 退款单信息 {}", JSONObject.toJSONString(refund));

        QueryOrderByOutTradeNoRequest req2 = new QueryOrderByOutTradeNoRequest();
        req2.setMchid("mch_id");
        req2.setOutTradeNo("out_trans_no");
        Transaction transaction = jsapiService.queryOrderByOutTradeNo(req2);
        log.info("transaction 支付单信息 {}", JSONObject.toJSONString(transaction));

        // h5 支付下单
        com.wechat.pay.java.service.payments.h5.model.PrepayRequest req = new com.wechat.pay.java.service.payments.h5.model.PrepayRequest();
        req.setMchid("mch_id");
        req.setAppid("app_id");
        com.wechat.pay.java.service.payments.h5.model.Amount amt = new com.wechat.pay.java.service.payments.h5.model.Amount();
        req.setAmount(amt);
        req.setDescription("订单描述");
        req.setAttach("order_attach");
        req.setNotifyUrl("回调地址");
        req.setOutTradeNo("out_trans_no");
        // 返回对象是 h5 连接，打开即可看到收银台页面
        com.wechat.pay.java.service.payments.h5.model.PrepayResponse prepay = h5Service.prepay(req);

        return "";
    }

    /**
     * 订单退款
     */
    @PostMapping(value = "/refund")
    public String wxOrderRefund(@RequestBody PayDto payDto){

        CreateRequest createRefund = new CreateRequest();
        // createRefund.setSubMchid(weixinConfig.getMchId());
        // 原交易单和本次退款单号
        createRefund.setOutTradeNo("orig_trade_no");
        createRefund.setOutRefundNo("refund_no");
        createRefund.setReason(payDto.getTitle() + "-退款");
        // 退款地址
        String refundUrl = "https://example.com/api/v1/weixin/refund/notify";
        createRefund.setNotifyUrl(refundUrl);
        AmountReq amountReq = new AmountReq();
        amountReq.setCurrency("CNY");
        amountReq.setTotal((long)transYu2Fen(payDto.getAmount()));
        amountReq.setRefund((long)transYu2Fen(payDto.getAmount()));
        createRefund.setAmount(amountReq);
        createRefund.setFundsAccount(ReqFundsAccount.AVAILABLE);
        log.info("refund request is {}", JSONObject.toJSONString(createRefund));
        Refund refund = refundService.create(createRefund);
        log.info("退款响应 {}", JSONObject.toJSONString(refund));

        return "";
    }


    // jsapi 支付场景
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
        request.setAttach("order-tag");
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
        JSONObject json = new JSONObject();
        json.put("timeStamp", timestamp);
        json.put("nonceStr", nonceStr);
        json.put("package", "prepay_id=" + prepayId);
        json.put("signType", "RSA");
        json.put("paySign", sign);
        log.info("result is {}", json);


    }

   // native 支付场景
    private String nativePay(PayDto payDto) {
        com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest request = new com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest();
        request.setAppid(weixinConfig.getAppid());
        request.setMchid(weixinConfig.getMchId());
        request.setDescription(payDto.getTitle());
        // 添加附加参数 附加数据
        request.setAttach("order-tag");
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




    private final static String WECHAT_PAY_SIGNATURE_TYPE = "Wechatpay-Signature-Type";

    // 组装请求对象
    private static RequestParam getRequestParam(HttpServletRequest request, String body) {
        String timestamp = request.getHeader(WECHAT_PAY_TIMESTAMP);
        String nonce = request.getHeader(WECHAT_PAY_NONCE);
        String serial = request.getHeader(WECHAT_PAY_SERIAL);
        String signature = request.getHeader(WECHAT_PAY_SIGNATURE);
        String signType = request.getHeader(WECHAT_PAY_SIGNATURE_TYPE);

        RequestParam param = new RequestParam.Builder()
                .serialNumber(serial)
                .nonce(nonce)
                .signature(signature)
                .timestamp(timestamp)
                .signType(signType)
                .body(body)
                .build();
        return param;
    }


















    // 微信支付回调
    @RequestMapping(value = "order/notify")
    public String orderNotify(HttpServletRequest request, HttpServletResponse response){

        Gson gson = new Gson();
        // 构造应答对象
        Map<String, String> map = new HashMap<>();
        try {
            // 1.处理通知参数
            String body = HttpUtils.readData(request);
            log.info("支付通知的完整数据:{}", body);
            RequestParam param = getRequestParam(request, body);
            log.info("支付验签参数 {}", param);
            Transaction transaction = parser.parse(param, Transaction.class);
            log.info("验签成功，支付回调结果为 {}", JSONObject.toJSONString(transaction));
            // 支付金额 流水号 支付状态 透传 attach
            TransactionAmount amount = transaction.getAmount();
            String transactionId = transaction.getTransactionId();
            Transaction.TradeStateEnum tradeState = transaction.getTradeState();
            String attach = transaction.getAttach();
            // 处理支付回调
            response.setStatus(200);
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);
        } catch (Exception e) {
            log.info("支付验签 err {} ", e.getMessage(), e);
            // 测试错误应答
            response.setStatus(500);
            map.put("code", "ERROR");
            map.put("message", "系统错误");
            return gson.toJson(map);
        }

    }

    // 微信退款回调
    @RequestMapping(value = "refund/notify")
    public String refundNotify(HttpServletRequest request, HttpServletResponse response){

        Gson gson = new Gson();
        // 构造应答对象
        Map<String, String> map = new HashMap<>();
        try {
            // 1.处理通知参数
            String body = HttpUtils.readData(request);
            log.info("退款通知的完整数据:{}", body);
            RequestParam param = getRequestParam(request, body);
            log.info("退款验签参数 {}", param);
            RefundNotification refund = parser.parse(param, RefundNotification.class);
            log.info("验签成功，退款回调结果为 {}", JSONObject.toJSONString(refund));
            // 处理退款回调 回调金额，退款单id 退款单号 原交易单号 退款状态
            com.wechat.pay.java.service.refund.model.Amount amount = refund.getAmount();
            String refundId = refund.getRefundId();
            String outRefundNo = refund.getOutRefundNo();
            String outTradeNo = refund.getOutTradeNo();
            Status refundStatus = refund.getRefundStatus();
            response.setStatus(200);
            map.put("code", "SUCCESS");
            map.put("message", "成功");
            return gson.toJson(map);
        } catch (Exception e) {
            log.info("error is {}", e.getMessage(), e);
            // 测试错误应答
            response.setStatus(500);
            map.put("code", "ERROR");
            map.put("message", "系统错误");
            return gson.toJson(map);
        }

    }


}
