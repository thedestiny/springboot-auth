package com.platform.utils.encdec;

import lombok.extern.slf4j.Slf4j;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-06-04 2:50 PM
 */

@Slf4j
public class ClientTest {


    public static void main(String[] args) {

        String appId = "1000";
        JSONObject child = new JSONObject();
        child.put("add", "234");
        child.put("egg", "34");
        JSONObject data = new JSONObject();
        data.put("name", "王思政");
        data.put("age", "12");
        data.put("data", child);
        data.put("address", "河南省郑州市");
        // todo step1 客户端加密加签
        ParamEncryptDto encrypt = CryptUtils.encrypt(data.toJSONString(), AppConfig.serverPubKey, AppConfig.clientPriKey);
        encrypt.setAppId(appId);
        log.info("client request {}", JSONObject.toJSONString(encrypt));

        // todo step2 服务端解密并验签
        String decrypt = CryptUtils.decrypt(encrypt);
        log.info("server receive data {}", decrypt);
        JSONObject jsonObject = JSONObject.parseObject(decrypt);
        // 添加回复信息
        jsonObject.put("receive", "收到");
        jsonObject.put("cellphone", "138xxxxxxx");
        jsonObject.put("idCard", "1234444");

        // todo step3 服务端签名并加密
        ParamEncryptDto encryptDto = CryptUtils.serverEncrypt(jsonObject.toJSONString(), appId);
        log.info("server response {}", JSONObject.toJSONString(encryptDto));

        // todo step4 客户端解密并验签
        String decrypt1 = CryptUtils.decrypt(JSONObject.toJSONString(encryptDto), AppConfig.serverPubKey, AppConfig.clientPriKey);
        JSONObject resp = JSONObject.parseObject(decrypt1);
        log.info("client receive {}", resp);


    }
}
