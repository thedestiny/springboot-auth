package com.platform.orderserver.controller;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.domain.*;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.*;
import com.alipay.api.response.*;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.ijpay.alipay.AliPayApi;
import com.platform.authcommon.utils.IdGenUtils;
import com.platform.orderserver.business.PayBusiness;
import com.platform.orderserver.config.AliPayConfig;
import com.platform.orderserver.dto.PayDto;
import com.platform.orderserver.utils.ResponseUtils;
import com.platform.orderserver.utils.ZxingUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping(value = "api/alipay")
public class AlipayController {

    @Autowired
    private AliPayConfig payConfig;
    @Autowired
    private AlipayClient client;
    @Autowired
    private PayBusiness payBusiness;

    /**
     * pc 网页支付
     */
    @GetMapping(value = "pc")
    public void pc(PayDto payDto, HttpServletResponse response) {
        // 回调通知地址
        String notifyUrl = payConfig.getNotify();
        AlipayTradePagePayRequest req = new AlipayTradePagePayRequest();
        // 通知地址和回调地址
        req.setNotifyUrl(notifyUrl);
        // req.setReturnUrl(notifyUrl);
        String orderNo = IdGenUtils.orderNo();
        AlipayTradePagePayModel model = new AlipayTradePagePayModel();
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(payDto.getAmount().toPlainString());
        model.setSubject(payDto.getSubject());
        model.setStoreId(payDto.getStoreId());
        model.setProductCode("FAST_INSTANT_TRADE_PAY");
        model.setTimeoutExpress("30m");
        req.setBizModel(model);
        AlipayTradePagePayResponse resp = null;
        try {
            resp = client.pageExecute(req);
            log.info("response result is {}", JSONObject.toJSONString(resp, true));
            String form = resp.getBody();
            log.info("响应信息:{}", form);
            // 直接将完整的表单html输出到页面
            JSONObject body = new JSONObject();
            body.put("form", form);
            body.put("orderNo", orderNo);
            ResponseUtils.response(response, body.toJSONString());

        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }
    }

    /**
     * 手机支付，收款码模式
     */
    @GetMapping(value = "wap")
    public void wap(PayDto payDto, HttpServletResponse response) {
        // 回调通知地址
        String notifyUrl = payConfig.getNotify();
        AlipayTradeWapPayRequest req = new AlipayTradeWapPayRequest();
        // 通知地址和回调地址
        req.setNotifyUrl(notifyUrl);
        // req.setReturnUrl(notifyUrl);
        String orderNo = IdGenUtils.orderNo();
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(orderNo);
        model.setTotalAmount(payDto.getAmount().toPlainString());
        model.setSubject(payDto.getSubject());
        model.setStoreId(payDto.getStoreId());
        model.setProductCode("QUICK_WAP_WAY");
        model.setTimeoutExpress("30m");
        req.setBizModel(model);

        AlipayTradeWapPayResponse resp = null;
        try {
            resp = client.pageExecute(req);
            log.info("result is {}", JSONObject.toJSONString(resp, true));
            String form = resp.getBody();
            log.info("响应信息:{}", form);
            // 直接将完整的表单html输出到页面
            JSONObject body = new JSONObject();
            body.put("form", form);
            body.put("orderNo", orderNo);
            ResponseUtils.response(response, body.toJSONString());


        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }
    }

    /**
     * 当面付支付
     */
    @GetMapping(value = "face")
    public String face(PayDto payDto) {
        String notifyUrl = payConfig.getNotify();
        String tradeNo = IdGenUtils.orderNo();
        AlipayTradePrecreateModel model = new AlipayTradePrecreateModel();
        model.setSubject(payDto.getSubject());
        model.setTotalAmount(payDto.getAmount().toPlainString());
        model.setStoreId(payDto.getStoreId());
        model.setTimeoutExpress("30m");
        model.setOutTradeNo(tradeNo);
        model.setBody("附加信息");
        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest();
        request.setNotifyUrl(notifyUrl);
        request.setBizModel(model);

        AlipayTradePrecreateResponse execute = null;
        try {
            execute = client.certificateExecute(request);
            log.info("result is {}", JSONObject.toJSONString(execute, true));
            // 支付链接，用于生成二维码
            String qrCode = execute.getQrCode();
            String name = IdGenUtils.id() + ".png";
            // 生成二维码
            ZxingUtils.createQRCodeImage(qrCode, "./" + name);

        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }

        return "";
    }

    /**
     * 订单通知
     */
    @RequestMapping(value = "order/notify")
    public String notifyUrl(HttpServletRequest request) {
        // 获取支付宝GET过来反馈信息
        JSONObject json = new JSONObject();
        Map<String, String> map = AliPayApi.toMap(request);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " = " + entry.getValue());
            json.put(entry.getKey(), entry.getValue());
        }
        log.info("notify result \n{}", json.toString(SerializerFeature.PrettyFormat));
        try {
            // 验签
            boolean verify = AlipaySignature.rsaCertCheckV1(map, payConfig.getAlipayCertPath(), "UTF-8",
                    "RSA2");
            if (verify) {
                // 业务处理
                return "success";
            } else {
                return "failure";
            }
        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
            return "failure";
        }
    }

    /**
     * 关闭订单
     */
    @GetMapping(value = "close/order")
    public String closeOrder(String orderNo) {
        payBusiness.closeOrder(orderNo);
        return "";
    }

    /**
     * 退款
     */
    @GetMapping(value = "refund/order")
    public String refundOrder(String orderNo) {
        String notifyUrl = payConfig.getNotify();
        try {
            AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
            AlipayTradeRefundModel model = new AlipayTradeRefundModel();
            // 原支付单号 退款金额 以及退款请求单号
            model.setOutTradeNo(orderNo);
            model.setRefundAmount("1");
            model.setOutRequestNo("123");
            // model.setTradeNo("");
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl);
            AlipayTradeRefundResponse response = client.certificateExecute(request);
            log.info("refund order result \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));

        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }
        return "";
    }

    /**
     * 订单查询
     */
    @GetMapping(value = "query/order")
    public String queryOrder(String orderNo) {
        payBusiness.queryOrder(orderNo);
        return "";
    }

}
