package com.platform.utils;


import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.URLUtil;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.URL;


public class HttpUtils {

    /**
     * 将通知参数转化为字符串
     * @param request
     * @return
     */
    public static String readData(HttpServletRequest request) {
        BufferedReader br = null;
        try {
            StringBuilder result = new StringBuilder();
            br = request.getReader();
            for (String line; (line = br.readLine()) != null; ) {
                if (result.length() > 0) {
                    result.append("\n");
                }
                result.append(line);
            }
            return result.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        URL url = URLUtil.url("http://localhost:8024/merchant/list");
        String jsonString = JSONObject.toJSONString(url);
        System.out.println(jsonString);
        ReflectUtil.setFieldValue(url, "authority", "gf-lc-wallet");
        System.out.println(url.toString());

    }
}
