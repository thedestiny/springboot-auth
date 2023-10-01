package com.platform.orderserver.business;


import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.platform.authcommon.exception.AppException;
import com.platform.orderserver.config.WeixinConfig;
import com.platform.orderserver.dto.PayDto;
import com.platform.orderserver.dto.PayResp;
import com.platform.orderserver.dto.RefundInfoDto;
import com.platform.orderserver.utils.WxApiType;
import com.platform.orderserver.utils.WxNotifyType;
import com.wechat.pay.contrib.apache.httpclient.util.AesUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class WxPayBusiness {

    @Autowired
    private WeixinConfig config;

    /**
     * 获取微信支付的httpClient，可以签名验签
     */
    @Autowired
    @Qualifier(value = "wxPayClient")
    private CloseableHttpClient payClient;

    /**
     * 获取微信支付的httpClient，不对响应进行验签
     */
    @Autowired
    @Qualifier(value = "wxPayNoSignClient")
    private CloseableHttpClient noSignClient;


    private final ReentrantLock lock = new ReentrantLock();


    /**
     * native 支付 api
     *
     * 开发指引：https://pay.weixin.qq.com/wiki/doc/apiv3/open/pay/chapter2_7_2.shtml
     * 接口文档：https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_4_1.shtml
     * requestJson
     * {
     * "amount":{
     * "total":1,
     * "currency":"CNY"
     * },
     * "mchid":"1558950191",
     * "out_trade_no":"ORDER_20220825104830065",
     * "appid":"wx74862e0dfcf69954",
     * "description":"test",
     * "notify_url":"https://500c-219-143-130-12.ngrok.io/api/wx-pay/native/notify"
     * }
     * <p>
     * response
     * {
     * "code": 0,
     * "message": "成功",
     * "data": {
     * "codeUrl": "weixin://wxpay/bizpayurl?pr=tyq42wrzz",
     * "orderNo": "ORDER_20220825104830065"
     * }
     * }
     */
    public PayResp nativePay(PayDto payDto) throws Exception {
        log.info("1.生成订单");
        PayResp resp = new PayResp();
        String orderNo = payDto.getOrderNo();
        resp.setOrderNo(orderNo);
        // 创建post请求 native 下单
        HttpPost httpPost = new HttpPost(config.getDomain().concat(WxApiType.NATIVE_PAY.getType()));

        // 构造请求参数
        // 这里请求参数很多，只传必填项就可以了，请求和响应都是json格式
        // gson是处理json的
        Gson gson = new Gson();
        // 你怎么知道要这些参数，参考文档啊 https://pay.weixin.qq.com/wiki/doc/apiv3_partner/apis/chapter4_4_1.shtml
        JSONObject paramsMap = new JSONObject();
        paramsMap.put("appid", config.getAppid());
        paramsMap.put("mchid", config.getMchId());
        paramsMap.put("description", payDto.getSubject());
        paramsMap.put("out_trade_no", orderNo);
        paramsMap.put("notify_url", config.getNotifyDomain().concat(WxNotifyType.NATIVE_NOTIFY.getType()));

        // 订单金额对象
        JSONObject amountMap = new JSONObject();
        // 此处的单位为分
        amountMap.put("total", transYu2Fen(payDto.getAmount()));
        amountMap.put("currency", "CNY");
        paramsMap.put("amount", amountMap);

        // 将参数转化成json字符串
        String requestJson = paramsMap.toJSONString();
        log.info("3.构造请求参数");
        log.info("请求参数：{}", requestJson);

        // 设置请求体及请求头
        StringEntity entity = new StringEntity(requestJson, "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        // 完成签名并执行请求 wxPayClient会自动的处理签名和验签，并进行证书自动更新
        CloseableHttpResponse httpResponse = payClient.execute(httpPost);
        log.info("4.解析微信native下单响应");
        try {
            // 获取响应体并转为字符串和响应状态码
            String response = EntityUtils.toString(httpResponse.getEntity());
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // 处理成功
                log.info("成功, 返回结果 = " + response);
            } else if (statusCode == 204) {
                // 处理成功，无返回Body
                log.info("成功");
            } else {
                log.info("Native下单失败,响应码 = " + statusCode + ",返回结果 = " + response);
                throw new IOException("request failed");
            }
            // 响应结果 json字符串转对象
            Map<String, String> resultMap = gson.fromJson(response, HashMap.class);
            // 获取二维码并保存
            String codeUrl = resultMap.get("code_url");
            log.info("5.响应二维码：{}，订单号：{}", codeUrl, orderNo);
            resp.setCodeUrl(codeUrl);
            return resp;
        } catch (Exception e) {
            log.error("error is {}", e.getMessage(), e);
        } finally {
            httpResponse.close();
        }
        return resp;
    }

    public static Integer transYu2Fen(BigDecimal amt) {
        BigDecimal mul = NumberUtil.mul(amt, 100);
        return mul.intValue();
    }

    /**
     * 处理订单
     *
     * @param bodyMap 支付通知参数
     * @throws GeneralSecurityException
     */
    public void processOrder(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("处理订单");

        // 1.密文解密
        String plainText = decryptFromResource(bodyMap);
        // 2.转换明文 https://pay.weixin.qq.com/wiki/doc/apiv3_partner/apis/chapter4_4_5.shtml

        Gson gson = new Gson();
        Map<String, Object> plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String) plainTextMap.get("out_trade_no");

        // 处理订单
        doHandleOutTimeOrder(plainTextMap, orderNo, plainText);

        /**
         * 在对业务数据进行状态检查和处理之前，这里要使用数据锁进行并发控制，以避免函数重入导致的数据混乱
         * 尝试获取锁成功之后才去处理数据，相比于同步锁，这里不会去等待，获取不到则直接返回
         */

    }

    /**
     * 用户取消订单
     */
    public void cancelOrder(String orderNo) throws IOException {
        // 调用微信支付的关单接口
        this.closeOrder(orderNo);
        //更新商户端的订单状态
    }

    /**
     * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_2.shtml
     * 文档上的path方法是指在url上的值，query则是参数
     * 查询订单调用
     */
    public String queryOrder(String orderNo) throws IOException {
        log.info("查单接口调用：{}", orderNo);
        String url = String.format(WxApiType.ORDER_QUERY_BY_NO.getType(), orderNo);
        url = config.getDomain().concat(url).concat("?mchid=").concat(config.getMchId());

        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");

        // 完成签名并执行请求
        CloseableHttpResponse response = payClient.execute(httpGet);

        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功，结果是：{}", bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功，无返回内容");
            } else {
                log.info("查询订单失败,响应码 = " + statusCode + ",返回结果 = " +
                        bodyAsString);
                throw new IOException("queryOrder request failed");
            }
            return bodyAsString;
        } finally {
            response.close();
        }
    }

    /**
     * 根据订单号查询微信支付查单接口，核实订单状态
     * 如果订单已支付，则更新商户端订单状态，并记录支付日志
     * 如果订单未支付，则调用关单接口关闭订单，并更新商户端订单状态
     *
     * @param orderNo
     */
    public void handleOutTimeOrder(String orderNo) throws IOException {
        log.warn("根据订单号核实订单状态 orderNo:{}", orderNo);
        // 1.调用微信支付查单接口
        String result = this.queryOrder(orderNo);

        // 2.转换响应参数
        Gson gson = new Gson();
        Map resultMap = gson.fromJson(result, HashMap.class);

        doHandleOutTimeOrder(resultMap, orderNo, result);

    }

    /**
     * 处理订单超时的情况
     */
    private void doHandleOutTimeOrder(Map resultMap, String orderNo, String result) {

    }

    /**
     * 申请退款，这个接口和文档不一样了，不知道能不能行呢
     *
     * @param resp
     */
    public void refund(RefundInfoDto resp) throws IOException {

        String orderNo = resp.getOrderNo();
        String refundNo = resp.getRefundNo();
        String reason = resp.getReason();
        BigDecimal orderAmount = resp.getOrderAmount();
        BigDecimal amount = resp.getAmount();

        log.info("调用微信退款接口");
        String url = config.getDomain().concat(WxApiType.DOMESTIC_REFUNDS.getType());
        HttpPost httpPost = new HttpPost(url);
        // 请求参数封装
        Gson gson = new Gson();
        Map<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("out_trade_no", orderNo);//订单编号
        paramsMap.put("out_refund_no", refundNo);//退款单编号
        paramsMap.put("reason", reason);//退款原因
        // 退款通知地址，退款也进行了回调通知，类似下单处理？
        paramsMap.put("notify_url", config.getNotifyDomain().concat(WxNotifyType.REFUND_NOTIFY.getType()));

        Map<String, Object> amountMap = new HashMap<String, Object>();
        amountMap.put("refund", transYu2Fen(amount));//退款金额
        amountMap.put("total", transYu2Fen(orderAmount));//原订单金额
        amountMap.put("currency", "CNY");//退款币种
        paramsMap.put("amount", amountMap);

        //将参数转换成json字符串
        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数:{}" + jsonParams);

        // 封装到请求中，并设置请求格式和响应格式
        StringEntity entity = new StringEntity(jsonParams, "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        // 发起退款请求，内部对请求做了签名，响应也验签了
        CloseableHttpResponse response = payClient.execute(httpPost);

        // 解析响应
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 退款返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("退款异常, 响应码 = " + statusCode + ", 退款返回结果 = " + bodyAsString);
            }

        } finally {
            response.close();
        }
    }

    /**
     * 查询退款使用
     */
    public String queryRefund(String refundNo) throws IOException {
        log.info("查询退款...");
        String url = config.getDomain().concat(String.format(WxApiType.DOMESTIC_REFUNDS_QUERY.getType(), refundNo));
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        CloseableHttpResponse response = payClient.execute(httpGet);
        // 解析响应
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 查询退款返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("查询退款异常, 响应码 = " + statusCode + ", 返回结果 = " + bodyAsString);
            }
            return bodyAsString;
        } finally {
            response.close();
        }
    }

    /**
     * 核实订单状态：调用微信支付查询退款接口
     */
    public void checkRefundStatus(String refundNo) throws IOException {
        // 1.查询退款订单
        String refund = this.queryRefund(refundNo);

        // 2.解析响应信息
        Gson gson = new Gson();
        Map<String, Object> refundMap = gson.fromJson(refund, HashMap.class);
        // 获取微信支付端退款状态
        String status = (String) refundMap.get("status");
        String orderNo = (String) refundMap.get("out_trade_no");

        log.info("refundNo is {} and result {} ", refundNo, refund);

    }


    @Transactional(rollbackFor = Exception.class)
    public void processRefund(Map<String, Object> dataMap) throws Exception {
        // 1.日志记录、上可重入锁
        log.info("处理退款订单...");

        // 2.转换响应中的密文
        String plainText = decryptFromResource(dataMap);
        // 将明文转换成map
        Gson gson = new Gson();
        HashMap plainTextMap = gson.fromJson(plainText, HashMap.class);
        String orderNo = (String) plainTextMap.get("out_trade_no");
        String refundNo = (String) plainTextMap.get("out_refund_no");
        String refundId = (String) plainTextMap.get("refund_id");
        String status = (String) plainTextMap.get("refund_status");

        // {"mchid":"1632966585","out_trade_no":"O1609084813795524608","transaction_id":"4200001674202212312032148146","out_refund_no":"R1609088684655771648","refund_id":"50302004462022123129290950753","refund_status":"SUCCESS","success_time":"2022-12-31T15:27:01+08:00","amount":{"total":1,"refund":1,"payer_total":1,"payer_refund":1},"user_received_account":"招商银行信用卡6642"}


    }

    /**
     * 获取交易账单URL
     */
    public String queryBill(String billDate, String type) throws IOException {
        // 1.日志记录
        log.info("请求微信获取交易账单下载地址...，日期是:{}", billDate);

        // 2.构造参数和请求
        String url = "";
        if ("tradebill".equals(type)) {
            url = WxApiType.TRADE_BILLS.getType();
        } else if ("fundflowbill".equals(type)) {
            url = WxApiType.FUND_FLOW_BILLS.getType();
        } else {
            throw new RuntimeException("不支持的账单类型");
        }

        // 3.处理响应获取需要的url
        url = config.getDomain().concat(url).concat("?bill_date=").concat(billDate);
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("Accept", "application/json");
        CloseableHttpResponse response = payClient.execute(httpGet);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 申请账单返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("申请账单异常, 响应码 = " + statusCode + ", 申请账单返回结果 = " + bodyAsString);
            }
            // 获取账单下载地址
            Gson gson = new Gson();
            Map<String, String> resultMap = gson.fromJson(bodyAsString, HashMap.class);
            return resultMap.get("download_url");
        } finally {
            response.close();
        }
    }

    public String downloadBill(String billDate, String type) throws IOException {
        // 1.日志记录
        log.info("下载{}的账单，类型是{}", billDate, type);

        // 2.获取交易账单URL
        String downloadUrl = this.queryBill(billDate, type);

        // 3.下载账单
        HttpGet httpGet = new HttpGet(downloadUrl);
        httpGet.addHeader("Accept", "application/json");
        // 这里不能对响应进行验签因为响应结果没有进行签名
        CloseableHttpResponse response = noSignClient.execute(httpGet);
        try {
            String bodyAsString = EntityUtils.toString(response.getEntity());
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                log.info("成功, 下载账单返回结果 = " + bodyAsString);
            } else if (statusCode == 204) {
                log.info("成功");
            } else {
                throw new RuntimeException("下载账单异常, 响应码 = " + statusCode + ", 下载账单返回结果 = " + bodyAsString);
            }
            return bodyAsString;
        } finally {
            response.close();
        }
    }


    /**
     * 关单接口调用
     * https://pay.weixin.qq.com/wiki/doc/apiv3/apis/chapter3_1_3.shtml
     * 以下情况需要调用关单接口：
     * 1、商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；
     * 2、系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
     *
     * @param orderNo
     */
    public void closeOrder(String orderNo) throws IOException {
        log.info("关单接口的调用，订单号：{}", orderNo);
        // 创建远程请求对象
        String url = String.format(WxApiType.CLOSE_ORDER_BY_NO.getType(), orderNo);
        url = config.getDomain().concat(url);
        HttpPost httpPost = new HttpPost(url);
        // 组装json请求体
        Gson gson = new Gson();
        Map<String, String> paramsMap = new HashMap<>();
        // 目前文档是有 服务商务号、子商户号，如果是 JSAPI则对得上
        paramsMap.put("mchid", config.getMchId());
        String jsonParams = gson.toJson(paramsMap);
        log.info("请求参数：{}", jsonParams);

        // 将请求参数设置到请求对象中
        StringEntity entity = new StringEntity(jsonParams, "utf-8");
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");

        // 完成签名并执行请求
        CloseableHttpResponse response = payClient.execute(httpPost);

        try {
            int statusCode = response.getStatusLine().getStatusCode();
            // 响应状态码
            if (statusCode == 200) {
                // 处理成功
                log.info("成功200");
            } else if (statusCode == 204) {
                // 处理成功，无返回Body
                log.info("成功204");
            } else {
                log.info("Native下单失败,响应码 = " + statusCode);
                throw new AppException("request failed");
            }
        } finally {
            response.close();
        }
    }

    /**
     * 对称解密
     * 为了保证安全性，微信支付在回调通知和平台证书下载接口中
     * 证书和回调报文使用的加密密钥为APIv3密钥
     * https://wechatpay-api.gitbook.io/wechatpay-api-v3/ren-zheng/api-v3-mi-yao
     */
    private String decryptFromResource(Map<String, Object> bodyMap) throws GeneralSecurityException {
        log.info("密文解密");
        // 获取通知数据中的resource，这部分有加密数据
        Map<String, String> resourceMap = (Map) bodyMap.get("resource");
        // 数据密文
        String ciphertext = resourceMap.get("ciphertext");
        // 随机串
        String nonce = resourceMap.get("nonce");
        // 附加数据
        String associatedData = resourceMap.get("associated_data");
        log.info("密文数据：{}", ciphertext);
        // 用APIv3密钥去解密
        AesUtil aesUtil = new AesUtil(config.getApiV3Key().getBytes(StandardCharsets.UTF_8));
        // 使用封装好的工具类去解密
        String plainText = aesUtil.decryptToString(
                associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                ciphertext);

        log.info("明文：{}", plainText);
        return plainText;
    }

}
