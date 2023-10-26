package com.platform.orderserver.controller;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.AlipayApiException;
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
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
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

    // 分账接口 https://opendocs.alipay.com/open/20190308105425129272/intro
    // 商户分账 需要开通商户分账的权限
    @PostMapping(value = "/trade/split")
    public String tradeSplit() {
        // 分账关系 需要建立数据库表维护分账关系
        List<OpenApiRoyaltyDetailInfoPojo> royaltyList = Lists.newArrayList();

        OpenApiRoyaltyDetailInfoPojo node = new OpenApiRoyaltyDetailInfoPojo();
        // 一般是 用户id 即支付宝用户id 分账类型为普通分账, 支付宝账户id默认 208 开头, 分账金额默认为元

        node.setRoyaltyType("transfer");
        node.setTransInType("userId");
        node.setTransInName(""); // 传入转账名称则进行校验，可以不传
        node.setTransIn("208861210241xxx");
        node.setAmount("2.98");
        node.setDesc("分账节点");
        // 分账转出方信息，传入信息和转入方要求一致，默认可以不填
        // node.setTransOut();
        // node.setTransOutType();

        royaltyList.add(node);
        String orderNo = IdGenUtils.orderNo();
        AlipayTradeOrderSettleModel model = new AlipayTradeOrderSettleModel();
        // 分账请求号和支付宝交易号
        model.setOutRequestNo(orderNo);
        model.setTradeNo("tradeNo");
        model.setRoyaltyParameters(royaltyList);
        AlipayTradeOrderSettleRequest request = new AlipayTradeOrderSettleRequest();
        request.setBizModel(model);

        try {
            // 发起分账
            AlipayTradeOrderSettleResponse response = client.certificateExecute(request);
            log.info("split order result \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));

        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }

        // 分账查询接口
        AlipayTradeOrderSettleQueryRequest settleQuery = new AlipayTradeOrderSettleQueryRequest();
        AlipayTradeOrderSettleQueryModel mod = new AlipayTradeOrderSettleQueryModel();
        // 传入结算单号或者 支付宝交易号和结算请求号
        mod.setSettleNo("settle_no");
        // mod.setTradeNo("trade_no");
        // mod.setOutRequestNo("out_req");
        settleQuery.setBizModel(mod);

        try {
            AlipayTradeOrderSettleQueryResponse response = client.certificateExecute(settleQuery);
            if (response.isSuccess()) {
                System.out.println("调用成功");
                System.out.println(JSONObject.toJSONString(response.getBody(), SerializerFeature.PrettyFormat));
            } else {
                System.out.println("调用失败");
            }
        } catch (Exception e) {
            log.error("error is {} and detail ", e.getMessage(), e);
        }

        return "";
    }

    /**
     * 分账关系绑定 解绑 查询
     */
    @PostMapping(value = "split/bind")
    public String bindSplit() {
        // 创建分账绑定关系
        AlipayTradeRoyaltyRelationBindRequest request = new AlipayTradeRoyaltyRelationBindRequest();
        AlipayTradeRoyaltyRelationBindModel m1 = new AlipayTradeRoyaltyRelationBindModel();
        // 分账请求流水号
        m1.setOutRequestNo("1222");
        List<RoyaltyEntity> receiverList = Lists.newArrayList();
        RoyaltyEntity entity = new RoyaltyEntity();
        entity.setAccount("支付宝id号");
        entity.setName("支付宝用户姓名");
        entity.setType("userId");
        entity.setMemo("分账给测试用户1");
        receiverList.add(entity);
        m1.setReceiverList(receiverList);
        request.setBizModel(m1);
        try {

            AlipayTradeRoyaltyRelationBindResponse response = client.certificateExecute(request);
            if (response.isSuccess()) {
                log.info("relation is \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        } catch (Exception e) {

        }
        // 分账关系查询
        AlipayTradeRoyaltyRelationBatchqueryRequest request1 = new AlipayTradeRoyaltyRelationBatchqueryRequest();
        AlipayTradeRoyaltyRelationBatchqueryModel m2 = new AlipayTradeRoyaltyRelationBatchqueryModel();
        // 分账关系查询关系，分页查询
        m2.setPageNum(1L);
        m2.setPageNum(20L);
        m2.setOutRequestNo("201903220000000112");
        request1.setBizModel(m2);
        try {
            AlipayTradeRoyaltyRelationBatchqueryResponse response = client.certificateExecute(request1);
            if (response.isSuccess()) {
                log.info("relation is \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 分账关系解绑
        AlipayTradeRoyaltyRelationUnbindRequest request2 = new AlipayTradeRoyaltyRelationUnbindRequest();
        AlipayTradeRoyaltyRelationUnbindModel m3 = new AlipayTradeRoyaltyRelationUnbindModel();
        // 解绑请求参数
        m3.setOutRequestNo("3333");
        // 解绑关系
        List<RoyaltyEntity> receiverList1 = Lists.newArrayList();
        RoyaltyEntity entity1 = new RoyaltyEntity();
        entity1.setAccount("支付宝id号");
        entity1.setName("支付宝用户姓名");
        entity1.setType("userId");
        entity1.setMemo("分账给测试用户1");
        receiverList1.add(entity1);
        m3.setReceiverList(receiverList1);

        try {
            AlipayTradeRoyaltyRelationUnbindResponse response = client.certificateExecute(request2);
            if (response.isSuccess()) {
                log.info("relation is \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return "";
    }

    // 转账接口 https://docs.open.alipay.com/309/106235/


    @PostMapping(value = "transfer")
    public String transfer() {
        AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();
        AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
        // 请求唯一单号 转账金额
        // 业务场景=单笔无密转账固定为 DIRECT_TRANSFER
        // 销售产品码=单笔无密转账固定为TRANS_ACCOUNT_NO_PWD
        model.setOutBizNo("201806300001");
        model.setTransAmount("23.00");
        model.setBizScene("DIRECT_TRANSFER");
        model.setProductCode("TRANS_ACCOUNT_NO_PWD");
        // 账单表题 展示付款方别名
        model.setOrderTitle("201905代发");
        model.setBusinessParams("{\"payer_show_name_use_alias\":\"true\"}");
        // 收款方信息 用户信息标识 用户真实名称
        Participant payeeInfo = new Participant();
        payeeInfo.setIdentityType("ALIPAY_USER_ID");
        payeeInfo.setIdentity("2088123412341234");
        payeeInfo.setName("黄龙国际有限公司");
        model.setPayeeInfo(payeeInfo);
        model.setRemark("201905代发");
        request.setBizModel(model);

        try {
            AlipayFundTransUniTransferResponse response = client.certificateExecute(request);
            if (response.isSuccess()) {
                log.info("relation is \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        // 转账信息查询
        AlipayFundTransCommonQueryRequest request1 = new AlipayFundTransCommonQueryRequest();
        AlipayFundTransCommonQueryModel model1 = new AlipayFundTransCommonQueryModel();
        model1.setOutBizNo("201808080001");
        model1.setOrderId("20190801110070000006380000250621");
        model1.setBizScene("PERSONAL_PAY");
        model1.setPayFundOrderId("20190801110070001506380000251556");
        model1.setProductCode("STD_RED_PACKET");
        request1.setBizModel(model1);

        try {
            AlipayFundTransCommonQueryResponse response = client.certificateExecute(request1);
            if (response.isSuccess()) {
                log.info("relation is \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));
                System.out.println("调用成功");
            } else {
                System.out.println("调用失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return "";
    }

}
