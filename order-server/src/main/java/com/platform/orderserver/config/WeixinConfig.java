package com.platform.orderserver.config;

import cn.hutool.core.io.resource.ClassPathResource;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.ScheduledUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;

@Slf4j
@Data // 使用set方法将wxpay节点中的值填充到当前类属性中
@Configuration
@ConfigurationProperties(prefix = "app.wxpay") // 读取wxpay节点
public class WeixinConfig {


    // 商户号 整数序列号 apiv3 密钥  应用appid 微信服务地址 回调地址
    private String mchId; //
    // 商户序列号
    private String mchSerialNo;
    // 私钥路径
    private String privateKeyPath;
    // api key
    private String apiV3Key;  //
    // 应用id
    private String appid; //
    // 微信服务地址
    private String domain;
    // 回调地址
    private String notifyDomain;


    private String partnerKey;

    /**
     * 微信SDK提供的获取httpClient方式会帮我们封装签名验签的方法等
     * https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay4_0.shtml
     * 1 构造签名串 2 计算签名值 3 设置 http 请求头 Authorization
     *
     */

    /**
     * 获取商户私钥
     * https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient
     */
    private PrivateKey getPrivateKey(String filename) {
        try {

            ClassPathResource resource = new ClassPathResource(filename);
            // return PemUtil.loadPrivateKey(new FileInputStream(filename));
            return PemUtil.loadPrivateKey(resource.getStream());
        } catch (Exception e) {
            throw new RuntimeException("私钥文件不存在", e);
        }
    }

    /**
     * 获取签名验证器 定时更新证书信息
     * https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient 有定时更新平台证书功能
     * 平台证书：平台证书封装了微信的公钥，商户可以使用平台证书中的公钥进行验签。
     * 签名验证器：帮助我们进行验签工作，我们单独将它定义出来，方便后面的开发
     */
    @Bean
    public ScheduledUpdateCertificatesVerifier getVerifier() {
        // 获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
        // 私钥签名对象（签名）
        PrivateKeySigner privateKeySigner = new PrivateKeySigner(mchSerialNo, privateKey);
        // 身份认证对象（验签）
        WechatPay2Credentials wechatPay2Credentials = new WechatPay2Credentials(mchId, privateKeySigner);
        // 使用定时更新的签名验证器，不需要传入证书
        ScheduledUpdateCertificatesVerifier verifier = new ScheduledUpdateCertificatesVerifier(
                wechatPay2Credentials,
                apiV3Key.getBytes(StandardCharsets.UTF_8));
        return verifier;
    }

    /**
     * 获取 httpClient 对象 实现了加密签名信息
     * https://github.com/wechatpay-apiv3/wechatpay-apache-httpclient （定时更新平台证书功能）
     * HttpClient 对象：是建立远程连接的基础，我们通过SDK创建这个对象
     */
    @Bean(name = "wxPayClient")
    public CloseableHttpClient wxPayClient(ScheduledUpdateCertificatesVerifier verifier) {
        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
        //用于构造HttpClient
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(mchId, mchSerialNo, privateKey)
                .withValidator(new WechatPay2Validator(verifier));
        // ... 接下来，你仍然可以通过builder设置各种参数，来配置你的HttpClient
        // 通过WechatPayHttpClientBuilder构造的HttpClient，会自动的处理签名和验签，并进行证书自动更新
        return builder.build();
//        CloseableHttpClient httpClient = builder.build();
//        return httpClient;
    }

    /**
     * 获取无需验证响应签名的httpClient对象
     */
    @Bean(name = "wxPayNoSignClient")
    public CloseableHttpClient wxPayNoSignClient() {
        log.info("初始化wxPayNoSignClient");
        //获取商户私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
        WechatPayHttpClientBuilder builder = WechatPayHttpClientBuilder.create()
                .withMerchant(mchId, mchSerialNo, privateKey)
                //设置响应对象无需签名
                .withValidator((response) -> true);
        CloseableHttpClient wxPayNoSignClient = builder.build();
        log.info("wxPayNoSignClient初始化完成");
        return wxPayNoSignClient;
    }
}
