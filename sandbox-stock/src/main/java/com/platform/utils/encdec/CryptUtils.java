package com.platform.utils.encdec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platform.common.ApiException;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-04 10:50 AM
 */

@Slf4j
public class CryptUtils {


    // 平台公钥和平台私钥
    private final static String app_pub_key = AppConfig.serverPubKey;
    private final static String app_pri_key = AppConfig.serverPriKey;
    // 客户端公钥映射 appId  - 客户端公钥
    private final static Map<String, String> client_pub_map = new HashMap<String, String>(){{
        // 客户端 appId = 1000
        put("1000", AppConfig.clientPubKey);
    }};

    /**
     * 服务端加密
     */
    public static ParamEncryptDto serverEncrypt(String response, String appId) {

        String appPublicKey = client_pub_map.get(appId);
        JSONObject jsonObject = JSONObject.parseObject(response);
        // 原始报文 json 格式化，规定排序格式
        String originalParamJsonStr = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField);
        // 签名
        String sign;
        try {
            // 我们私钥加签名
            sign = RSAUtils.sign(originalParamJsonStr, app_pri_key);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("response RSA 签名失败：{}", e.getMessage());
            throw new ApiException("签名失败!", e);
        }
        //生成的签名sign添加到原始参数中
        jsonObject.put("sign", sign);

        //加签名后的参数转JSON串
        String originalParamSignJsonStr = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField);

        // 加签后的参数AES加密
        //随机生成AES密码
        String aesKey = UUID.randomUUID().toString();
        //加签后的参数AES加密
        String encryptData = AESUtil.encrypt(originalParamSignJsonStr, aesKey);
        // 随机AES密码进行RSA加密
        String encryptKey = null;
        try {
            encryptKey = RSAUtils.publicEncrypt(aesKey, appPublicKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidKeySpecException e) {
            log.warn("快看看-response RSA 加密失败：{}", e.getMessage());
            throw new ApiException("加密失败!", e);
        }
        ParamEncryptDto encryptDto = new ParamEncryptDto();
        encryptDto.setEncryptKey(encryptKey);
        encryptDto.setEncryptData(encryptData);
        encryptDto.setAppId(appId);

        return encryptDto;

    }


    /**
     * 客户端加密
     */
    public static ParamEncryptDto encrypt(String response, String appPublicKey, String selfPriKey) {

        JSONObject jsonObject = JSONObject.parseObject(response);
        // 原始报文 json 格式化，规定排序格式
        String originalParamJsonStr = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField);
        // 签名
        String sign;
        try {
            // 我们私钥加签名
            sign = RSAUtils.sign(originalParamJsonStr, selfPriKey);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("response RSA 签名失败：{}", e.getMessage());
            throw new ApiException("签名失败!", e);
        }
        //生成的签名sign添加到原始参数中
        jsonObject.put("sign", sign);

        //加签名后的参数转JSON串
        String originalParamSignJsonStr = JSON.toJSONString(jsonObject, SerializerFeature.MapSortField);

        // 加签后的参数AES加密
        //随机生成AES密码
        String aesKey = UUID.randomUUID().toString();
        //加签后的参数AES加密
        String encryptData = AESUtil.encrypt(originalParamSignJsonStr, aesKey);
        // 随机AES密码进行RSA加密
        String encryptKey = null;
        try {
            encryptKey = RSAUtils.publicEncrypt(aesKey, appPublicKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException | InvalidKeySpecException e) {
            log.warn("快看看-response RSA 加密失败：{}", e.getMessage());
            throw new ApiException("加密失败!", e);
        }
        ParamEncryptDto encryptDto = new ParamEncryptDto();
        encryptDto.setEncryptKey(encryptKey);
        encryptDto.setEncryptData(encryptData);
        return encryptDto;

    }



    /**
     * 服务端解密
     */
    public static String decrypt(ParamEncryptDto body) {

        String appId = body.getAppId();
         if(StrUtil.isBlank(appId)){
             throw new ApiException("appId 不能为空!");
         }

        // 加密返回值
        String responseStr = JSON.toJSONString(body, SerializerFeature.MapSortField);
        // 获取加密返回值encryptKey和encryptData appId
        JSONObject walletResponseObj = JSON.parseObject(responseStr);

        String encryptKey = walletResponseObj.getString("encryptKey");
        String encryptData = walletResponseObj.getString("encryptData");
        // 解密得到AES的key 我们自己的私钥 外部私钥解密
        String aesKey = "";
        try {
            aesKey = RSAUtils.privateDecrypt(encryptKey, app_pri_key);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("request RSA 解密失败：{}", e.getMessage());
            throw new ApiException("解密失败!");
        }

        // aes的key解密加密回参
        String origResponseStr = AESUtil.decrypt(encryptData, aesKey);

        JSONObject origResponseJson = JSON.parseObject(origResponseStr);

        // 回参验签
        String sign = origResponseJson.getString("sign");
        if (StringUtils.isBlank(sign)) {
            throw new ApiException("签名不能为空!");
        }
        origResponseJson.remove("sign");
        // 移除签名后的结果
        String origResponseNoSignStr = JSON.toJSONString(origResponseJson, SerializerFeature.MapSortField);
        // 获取客户端的公钥
        String publicKey = client_pub_map.get(appId);
        //钱包公钥验签
        boolean signVerify = false;
        try {
            signVerify = RSAUtils.verify(origResponseNoSignStr, publicKey, sign);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("request RSA 验证失败：{}", e.getMessage());
            throw new ApiException("验签失败!", e);
        }

        if (signVerify) {
            return origResponseNoSignStr;
        } else {
            log.warn("origin request:{}, request RSA验证结果: false.", origResponseStr);
            throw new ApiException("验签结果失败!");
        }
    }

    /**
     * 客户端解密
     */
    public static String decrypt(String body, String publicKey, String selfPriKey) {

        // 加密返回值
        // 获取加密返回值encryptKey和encryptData appId
        JSONObject walletResponseObj = JSON.parseObject(body);

        String encryptKey = walletResponseObj.getString("encryptKey");
        String encryptData = walletResponseObj.getString("encryptData");
        // 解密得到AES的key 我们自己的私钥 外部私钥解密
        String aesKey = "";
        try {
            aesKey = RSAUtils.privateDecrypt(encryptKey, selfPriKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("request RSA 解密失败：{}", e.getMessage());
            throw new ApiException("解密失败!");
        }

        // aes的key解密加密回参
        String origResponseStr = AESUtil.decrypt(encryptData, aesKey);

        JSONObject origResponseJson = JSON.parseObject(origResponseStr);

        // 回参验签
        String sign = origResponseJson.getString("sign");
        if (StringUtils.isBlank(sign)) {
            throw new ApiException("签名不能为空!");
        }
        origResponseJson.remove("sign");
        // 移除签名后的结果
        String origResponseNoSignStr = JSON.toJSONString(origResponseJson, SerializerFeature.MapSortField);
        // 获取客户端的公钥
        //钱包公钥验签
        boolean signVerify = false;
        try {
            signVerify = RSAUtils.verify(origResponseNoSignStr, publicKey, sign);
        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException | InvalidKeySpecException e) {
            log.warn("request RSA 验证失败：{}", e.getMessage());
            throw new ApiException("验签失败!", e);
        }

        if (signVerify) {
            return origResponseNoSignStr;
        } else {
            log.warn("origin request:{}, request RSA验证结果: false.", origResponseStr);
            throw new ApiException("验签结果失败!");
        }
    }

}
