package com.platform.contract.esign;


import com.platform.contract.config.HeaderConstant;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class EsignHttpHelper {

    private static final String SDK_VERSION = "Esign-Sdk-Core1.0";

    private EsignHttpHelper() {
    }

    public static Map<String, String> signAndBuildSignAndJsonHeader(String httpMethod, String projectId, String secret, String paramStr, String url) {
        String contentMD5 = "";
        httpMethod = httpMethod.toUpperCase();
        if (StringUtils.equalsAny(httpMethod, new CharSequence[]{"DELETE", "GET"})) {
            String var10 = null;
            contentMD5 = "";
        } else {
            if (!StringUtils.equalsAny(httpMethod, new CharSequence[]{"POST", "PUT"})) {
                throw new ApiException(ApiCodeEnum.ERROR_BIZ, String.format("不支持的请求方法%s", httpMethod), new Object[0]);
            }

            contentMD5 = EsignEncryption.doContentMD5(paramStr);
        }

        Map<String, String> esignHeaderMap = buildSignAndJsonHeader(projectId, contentMD5, HeaderConstant.ACCEPT.VALUE(), HeaderConstant.CONTENTTYPE_JSON.VALUE(), HeaderConstant.AUTHMODE.VALUE());
        url = EsignEncryption.sortApiUrl(url);
        String message = EsignEncryption.appendSignDataString(httpMethod, (String)esignHeaderMap.get("Content-MD5"), (String)esignHeaderMap.get("Accept"), (String)esignHeaderMap.get("Content-Type"), (String)esignHeaderMap.get("Headers"), (String)esignHeaderMap.get("Date"), url);
        String reqSignature = EsignEncryption.doSignatureBase64(message, secret);
        esignHeaderMap.put("X-Tsign-Open-Ca-Signature", reqSignature);
        return esignHeaderMap;
    }

    static void main(String[] args) {
        String httpMethod = "POST";
        String projectId = "7439141928";
        String secret = "29886371cce21aa683fc121654dfd11d";
        String paramStr = "{\"contentMd5\":\"kTkzxgK8WmCfW5X92Ppr1w==\",\"fileName\":\"软件销售合同2026.pdf\",\"fileSize\":127726,\"convertToPDF\":false,\"contentType\":\"application/octet-stream\"}";
        String url = "/v3/files/file-upload-url";
        Map<String, String> header = signAndBuildSignAndJsonHeader(httpMethod, projectId, secret, paramStr, url);

        for(Map.Entry<String, String> entry : header.entrySet()) {
            log.info("requestJson header key {} value: {}", entry.getKey(), entry.getValue());
        }

    }

    public static Map<String, String> buildSignAndJsonHeader(String projectId, String contentMD5, String accept, String contentType, String authMode) {
        Map<String, String> header = new HashMap();
        header.put("X-Tsign-Open-App-Id", projectId);
        header.put("X-Tsign-Open-Version-Sdk", "Esign-Sdk-Core1.0");
        header.put("X-Tsign-Open-Ca-Timestamp", EsignEncryption.timeStamp());
        header.put("Accept", accept);
        header.put("Content-MD5", contentMD5);
        header.put("Content-Type", contentType);
        header.put("X-Tsign-Open-Auth-Mode", authMode);
        return header;
    }

    public static Map<String, String> buildUploadHeader(String fileContentMd5, String contentType) {
        Map<String, String> header = new HashMap();
        header.put("Content-MD5", fileContentMd5);
        header.put("Content-Type", contentType);
        return header;
    }
}
