package com.platform.orderserver.config;

import com.alipay.api.*;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AliPayConfig {

    // 私钥
    private String privateKey;
    // 应用证书
    private String appCertPath;
    // 支付宝证书
    private String alipayCertPath;
    // 支付宝根证书
    private String alipayRootCertPath;
    // 应用id
    private String appId;
    // 回调地址
    private String notify;
    // 字符集 和 签名方式
    private String charset = "UTF-8";
    private String signType = "RSA2";
    private String format = "json";
    private String gateway = "https://openapi.alipay.com/gateway.do";
    // 支付宝公钥
    private String alipayPublicKey = "";

    // 公私钥模式
    @Bean
    public AlipayClient alipayClient() {

        return new DefaultAlipayClient(this.gateway, this.appId, this.privateKey,
                AlipayConstants.FORMAT_JSON, AlipayConstants.CHARSET_UTF8,
                this.alipayPublicKey, AlipayConstants.SIGN_TYPE_RSA2);

    }

    // 证书模式
    @Bean
    public AlipayClient certAlipayRequest() {
        CertAlipayRequest request = new CertAlipayRequest();
        request.setServerUrl(gateway);//服务器网址
        request.setAppId(appId);//支付宝分配给开发者的应用ID
        request.setPrivateKey(privateKey); // 私钥
        request.setFormat(format);//请求格式
        request.setCharset(charset);//编码
        request.setSignType(signType);//商户生成签名字符串所使用的签名算法类型
        request.setCertPath(appCertPath);   //应用证书地址
        request.setAlipayPublicCertPath(alipayCertPath);
        request.setRootCertPath(alipayRootCertPath);
        DefaultAlipayClient client = null;
        try {
            client = new DefaultAlipayClient(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return client;
    }
}
