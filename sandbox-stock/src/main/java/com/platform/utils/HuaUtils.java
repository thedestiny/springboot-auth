package com.platform.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.platform.entity.EtfInfo;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.List;

public class HuaUtils {

    // ETF 机会
    // http://fund.10jqka.com.cn/quotation/open/api/select/tool/v1/cache/index_opportunity_value
    /**
     * 同花顺全球 etf t+0
     */
    private static final String url = "http://eq.10jqka.com.cn/eq/open/api/homepage_v2/v2/card/yekaquanqiuliebiao";


    public static void main(String[] args) {


        // System.out.println(json);
        List<EtfInfo> res = captureEtf();


    }


    public static List<EtfInfo> captureEtf() {
        HttpRequest request = HttpUtil.createGet(url);

        String body = request.execute().body();
        JSONObject json = JSONObject.parseObject(body);
        JSONObject data = json.getJSONArray("data").getJSONObject(0);
        JSONArray themes = data.getJSONArray("themes");
        // 全球 和 行业
        JSONObject global = themes.getJSONObject(0);
        JSONObject industry = themes.getJSONObject(1);
        JSONArray regions1 = global.getJSONArray("regions");
        JSONArray regions2 = industry.getJSONArray("regions");

        JSONArray result = new JSONArray();
        for (int i = 0; i < regions1.size(); i++) {
            JSONObject region = regions1.getJSONObject(i);
            result.add(region);
        }
        for (int i = 0; i < regions2.size(); i++) {
            JSONObject region = regions2.getJSONObject(i);
            result.add(region);
        }
        List<EtfInfo> res = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            JSONObject node = result.getJSONObject(i);
            String name = node.getString("name");
            String ket = node.getString("key");
            if(ket.equals("global")){
                continue;
            }
            JSONArray etfList = node.getJSONArray("etfList");
            for (int i1 = 0; i1 < etfList.size(); i1++) {
                JSONObject etf = etfList.getJSONObject(i1);
                String code = etf.getString("code");
                String market = etf.getString("market");
                System.out.println(name + " " + code + " " + market);
                EtfInfo ele = new EtfInfo();
                ele.setCode(code);
                ele.setMarket(market);
                ele.setDetail(name);
                res.add(ele);
            }
        }

        return res;
    }
}
