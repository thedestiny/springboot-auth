package com.platform.orderserver.business;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alipay.api.AlipayClient;
import com.alipay.api.domain.AlipayTradeCloseModel;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.request.AlipayTradeCloseRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCloseResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.platform.orderserver.config.AliPayConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2023-07-14 5:05 PM
 */
@Slf4j
@Component
public class PayBusiness {


    @Autowired
    private AliPayConfig payConfig;
    @Autowired
    private AlipayClient client;

    /**
     * 关闭订单
     */
    public void closeOrder(String orderNo){

        String notifyUrl = payConfig.getNotify();
        try {
            AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
            AlipayTradeCloseModel model = new AlipayTradeCloseModel();
            model.setOutTradeNo(orderNo);
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl);
            AlipayTradeCloseResponse response = client.certificateExecute(request);
            log.info("close result \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));

        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }
    }
    /**
     * 查询订单
     */
    public String queryOrder(String orderNo) {
        String notifyUrl = payConfig.getNotify();
        try {
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            AlipayTradeQueryModel model = new AlipayTradeQueryModel();
            // 原支付单号 退款金额 以及退款请求单号
            model.setOutTradeNo(orderNo);
            // model.setTradeNo("");
            request.setBizModel(model);
            request.setNotifyUrl(notifyUrl);
            AlipayTradeQueryResponse response = client.certificateExecute(request);
            log.info("query order result \n{}", JSONObject.toJSONString(response, SerializerFeature.PrettyFormat));

        } catch (Exception e) {
            log.info("error is {} and e ", e.getMessage(), e);
        }
        return "";
    }

}
