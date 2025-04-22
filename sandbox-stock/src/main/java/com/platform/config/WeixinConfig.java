package com.platform.config;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.refund.RefundService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-04-22 6:36 PM
 */

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "app.weixin")
public class WeixinConfig {

    // 商户号
    private String mchId;
    // 商户序列号
    private String mchSerialNo;
    // 私钥路径
    private String privateKeyPath;
    // api key
    private String apiV3Key;
    // 应用id
    private String appid;
    // 微信服务地址
    private String domain;
    // 回调地址
    private String notifyDomain;

    private String pubKeyId;

    private String pubKeyPath;

    @Value("${spring.profiles.active}")
    private String env;


    // 退款service
    @Bean("wxRefundService")
    public RefundService refundService() {
        log.info("refund service ");
        return new RefundService.Builder().config(config()).build();
    }

    @Bean("jsapiService")
    public JsapiService jsapiService() {
        log.info("jsapi service ");
        return new JsapiService.Builder().config(config()).build();
    }

    // native 支付方式
    @Bean("nativePayService")
    public NativePayService nativePayService() {
        log.info("native pay service ");
        return new NativePayService.Builder().config(config()).build();
    }


    @Bean
    public Config config() {
        String pri = "";
        String pub = "";
        try {
            ClassPathResource priPath = new ClassPathResource(privateKeyPath);
            pri = priPath.getAbsolutePath();
            log.info("private key path {}", priPath.getAbsolutePath());
            ClassPathResource pubPath = new ClassPathResource(pubKeyPath);
            log.info("public key path {}", pubPath.getAbsolutePath());
            pub = pubPath.getAbsolutePath();

        } catch (Exception e) {
            log.info("classpath load error {}", e.getMessage());
        }
        if (StrUtil.equalsAny(env, "prd", "test")) {
            pri = "/root/sandbox-app/apiclient_key.pem";
            pub = "/root/sandbox-app/pub_key.pem";
        }
        log.info("load key path pri {} pub {} ", pri, pub);
        // 微信支付api
        // https://github.com/wechatpay-apiv3/wechatpay-java
        Config config =
                new RSAPublicKeyConfig.Builder()
                        .merchantId(mchId) //微信支付的商户号
                        .privateKeyFromPath(pri) // 商户API证书私钥的存放路径
                        .publicKeyFromPath(pub) //微信支付公钥的存放路径
                        .publicKeyId(pubKeyId) //微信支付公钥ID
                        .merchantSerialNumber(mchSerialNo) //商户API证书序列号
                        .apiV3Key(apiV3Key) //APIv3密钥
                        .build();

        return config;
    }


    // jsapi 签名
    public String jsApiPaySign(String timestamp, String nonceStr, String prepayId) throws Exception {
        //上传私钥
        PrivateKey privateKey = getPrivateKey(privateKeyPath);
        String signatureStr = Stream.of(appid, timestamp, nonceStr, "prepay_id=" + prepayId)
                .collect(Collectors.joining("\n", "", "\n"));
        System.out.println(signatureStr);
        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(privateKey);
        sign.update(signatureStr.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(sign.sign());
    }
    private PrivateKey getPrivateKey(String filename) {
        // 可以根据实际情况使用publicKeyFromPath或publicKey加载公
        try {
            ClassPathResource resource = new ClassPathResource(filename);
            // return PemUtil.loadPrivateKey(new FileInputStream(filename));
            return PemUtil.loadPrivateKey(resource.getStream());
        } catch (Exception e) {
            throw new RuntimeException("私钥文件不存在", e);
        }
    }

}
