package com.platform.orderserver.controller;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.platform.orderserver.config.AppKeyConfig;
import com.platform.orderserver.config.PriPubKeyConfig;
import com.platform.orderserver.dto.DataReq;
import com.platform.orderserver.utils.RSAUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * https://www.python100.com/html/20551.html#%E7%94%A8openssl%20%E5%92%8C%20keytool%20%E7%94%9F%E6%88%90%20SSL%E8%AF%81%E4%B9%A6
 */
@Slf4j
@Api(tags = "加解密签名")
@RestController
@RequestMapping("/api/key")
public class AppController {

    @Autowired
    private PriPubKeyConfig config;

    @Autowired
    private AppKeyConfig keyConfig;

    @GetMapping(value = "data")
    public String data(){
        int i = 1/0;
        return "success";
    }

    /**
     * server 服务端
     */
    @PostMapping(value = "server")
    public String server(@RequestBody DataReq dataReq) {
        // 获取加密报文，应用id 签名sign
        String encrypt = dataReq.getEncrypt();
        String appId = dataReq.getAppId();
        String sign = dataReq.getSign();
        String s1 = JSONObject.toJSONString(dataReq);
        // 打印内容
        log.info("server request \n{} ", JSONObject.toJSONString(s1, SerializerFeature.PrettyFormat));
        // 根据应用id 获取公钥, 使用对方的公钥进行验签
        boolean verify = RSAUtils.verify(encrypt, sign, queryPubKey(appId));
        if (verify) {
            // 解密
            String decrypt = RSAUtils.decrypt(encrypt, config.getServerPriKey());
            JSONObject jsonObject = JSONObject.parseObject(decrypt);
            log.info("server 验签通过, 解密报文 \n{} ", jsonObject.toString(SerializerFeature.PrettyFormat));
            JSONObject resp = new JSONObject();
            resp.put("code", "0000");
            resp.put("name", "小明");
            resp.put("age", "24");
            resp.put("address", "中国北京市朝阳区");
            // 加密报文，加密报文需要将明文进行字典排序
            String encrypt1 = RSAUtils.encrypt(resp, queryPubKey(appId));
            Map<String, Object> maps = new HashMap<>();
            maps.put("encrypt", encrypt1);
            maps.put("appId", appId);
            // 签名内容同样需要按照字典顺序排序
            String sign1 = RSAUtils.sign(maps, config.getServerPriKey());
            maps.put("sign", sign1);
            // 返回内容
            String s = JSONObject.toJSONString(maps);
            log.info("server 返回报文 \n{}", JSONObject.toJSONString(s, true));
            return s;
        }

        return new JSONObject().toString();
    }

    /**
     * 根据 appId 获取公钥信息
     *
     * @param appId
     * @return
     */
    private String queryPubKey(String appId) {
        String key = keyConfig.getKeys().get(appId);
        if (StrUtil.isBlank(key)) {
            throw new RuntimeException("没有找到 appId");
        }
        return key;
    }


    // localhost:8080/api/key/client
    @GetMapping(value = "client")
    public String client() {
        String appId = "123456789";
        JSONObject resp = new JSONObject();
        resp.put("code", "0");
        resp.put("name", "小李");
        resp.put("age", "12");
        resp.put("address", "中国天津市武清区");
        // 按照字典排序
        String str = JSONObject.toJSONString(resp, SerializerFeature.MapSortField);
        // 加密 使用服务方的公钥进行加密
        String encrypt = RSAUtils.encrypt(str, config.getServerPubKey());
        // 签名 对加密报文使用客户端私钥进行签名
        String sign = RSAUtils.sign(encrypt, config.getClientPriKey());
        DataReq req = new DataReq();
        req.setAppId(appId);
        req.setEncrypt(encrypt);
        req.setSign(sign);
        // 发送请求
        HttpRequest post = HttpUtil.createPost("localhost:8080/api/key/server");
        post.header("Content-Type", "application/json;charset=UTF-8");
        // 设置请求报文
        post.body(JSONObject.toJSONString(req));
        log.info("client req body \n{}", JSONObject.toJSONString(req, true));
        HttpResponse execute = post.execute();
        String body = execute.body();
        JSONObject jsonObject2 = JSONObject.parseObject(body);
        log.info("server response \n{}", JSONObject.toJSONString(jsonObject2, SerializerFeature.PrettyFormat));
        JSONObject jsonObject = JSONObject.parseObject(body);
        String en = jsonObject.getString("encrypt");
        String si = jsonObject.getString("sign");

        Map<String, Object> maps = new TreeMap<>();
        maps.put("encrypt", en);
        maps.put("appId", appId);
        // 验证签名
        boolean verify = RSAUtils.verify(JSONObject.toJSONString(maps), si, config.getServerPubKey());
        if (verify) {
            // 解密报文
            String decrypt = RSAUtils.decrypt(en, config.getClientPriKey());
            JSONObject jsonObject1 = JSONObject.parseObject(decrypt, Feature.OrderedField);
            log.info("client 验签通过， 解密服务端报文 \n{}", jsonObject1.toString(SerializerFeature.PrettyFormat));
        }
        return "success";
    }

}
