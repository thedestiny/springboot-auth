package com.platform.contract.esign;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class EsignEncryption {

    private EsignEncryption() {
    }

    public static String appendSignDataString(String httpMethod, String contentMd5, String accept, String contentType, String headers, String date, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(httpMethod).append("\n").append(accept).append("\n").append(contentMd5).append("\n").append(contentType).append("\n");
        if (!"".equals(date) && date != null) {
            sb.append(date).append("\n");
        } else {
            sb.append("\n");
        }

        if (!"".equals(headers) && headers != null) {
            sb.append(headers).append("\n").append(url);
        } else {
            sb.append(url);
        }

        return sb.toString();
    }

    public static String doContentMD5(String str) {

        try {

            // md5 1
            String md5Hex = DigestUtil.md5Hex(str);
            System.out.println(Base64.encodeBase64String(HexUtil.decodeHex(md5Hex)));

            // md5 2
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes("UTF-8"));
            byte[] md5Bytes = md5.digest();
            String contentMD5 = Base64.encodeBase64String(md5Bytes);
            return contentMD5;
        } catch (NoSuchAlgorithmException var4) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "不支持此算法", str);
        } catch (UnsupportedEncodingException var5) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "UTF-8 编码不支持", str);
        }
    }

    public static String doSignatureBase64(String message, String secret) {

        String algorithm = "HmacSHA256";

        try {
            Mac hmacSha256 = Mac.getInstance(algorithm);
            byte[] keyBytes = secret.getBytes("UTF-8");
            byte[] messageBytes = message.getBytes("UTF-8");
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, algorithm));
            byte[] digestBytes = hmacSha256.doFinal(messageBytes);
            String digestBase64 = Base64.encodeBase64String(digestBytes);
            return digestBase64;
        } catch (NoSuchAlgorithmException var8) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "不支持此算法", message);
        } catch (InvalidKeyException var9) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "无效的密钥规范", message);
        } catch (UnsupportedEncodingException var10) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "UTF-8 编码不支持", message);
        }
    }

    public static String timeStamp() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String sortApiUrl(String apiUrl) {
        if (!apiUrl.contains("?")) {
            return apiUrl;
        } else {
            int queryIndex = apiUrl.indexOf("?");
            String apiUrlPath = apiUrl.substring(0, queryIndex + 1);
            String apiUrlQuery = apiUrl.substring(queryIndex + 1);
            if (StringUtils.isBlank(apiUrlQuery)) {
                return apiUrl.substring(0, apiUrl.length() - 1);
            } else {
                Map<Object, Object> queryParamsMap = new HashMap();
                String[] params = apiUrlQuery.split("&");

                for(String str : params) {
                    int index = str.indexOf("=");
                    String key = str.substring(0, index);
                    String value = str.substring(index + 1);
                    if (queryParamsMap.containsKey(key)) {
                        throw new ApiException(ApiCodeEnum.ERROR_BIZ, "请求URL中的Query参数的{0}重复", new Object[]{key});
                    }

                    queryParamsMap.put(key, value);
                }

                List<String> queryMapKeys = new ArrayList<>();

                for(Map.Entry<Object, Object> entry : queryParamsMap.entrySet()) {
                    queryMapKeys.add((String)entry.getKey());
                }

                Collections.sort(queryMapKeys, new Comparator<String>() {
                    public int compare(String o1, String o2) {
                        return o1.compareToIgnoreCase(o2) == 0 ? -o1.compareTo(o2) : o1.compareToIgnoreCase(o2);
                    }
                });

                StringBuilder queryString = new StringBuilder();

                for(int i = 0; i < queryMapKeys.size(); ++i) {
                    String key = (String)queryMapKeys.get(i);
                    String value = (String)queryParamsMap.get(key);
                    queryString.append(key);
                    queryString.append("=");
                    queryString.append(value);
                    queryString.append("&");
                }

                if (queryString.length() > 0) {
                    queryString = queryString.deleteCharAt(queryString.length() - 1);
                }

                StringBuilder sortApiUrl = new StringBuilder();
                sortApiUrl.append(apiUrlPath);
                sortApiUrl.append(queryString.toString());
                return sortApiUrl.toString();
            }
        }
    }

    public static boolean callBackCheck(String timestamp, String requestQuery, String body, String key, String signature) {
        String algorithm = "HmacSHA256";
        String encoding = "UTF-8";

        try {
            String data = timestamp + requestQuery + body;
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(encoding), algorithm);
            mac.init(secretKey);
            mac.update(data.getBytes(encoding));
            return byte2hex(mac.doFinal()).equalsIgnoreCase(signature);
        } catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException var10) {
            return false;
        }
    }

    public static String byte2hex(byte[] data) {
        StringBuilder hash = new StringBuilder();

        for(int n = 0; data != null && n < data.length; ++n) {
            String stmp = Integer.toHexString(data[n] & 255);
            if (stmp.length() == 1) {
                hash.append('0');
            }

            hash.append(stmp);
        }

        return hash.toString();
    }
}
