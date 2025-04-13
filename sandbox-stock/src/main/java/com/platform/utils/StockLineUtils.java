package com.platform.utils;

import java.math.BigDecimal;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.platform.dto.StockLineDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class StockLineUtils {


    private static final String server = "http://54.push2his.eastmoney.com/api/qt/stock/kline/get";

    public static List<StockLineDto> queryKlineData(String code) {
        String start = "20220101";
        String end = "20300101";
        String klt = "101";
        return queryKlineData(code, start, end, klt);
    }

    public static List<List<Object>> queryKlineList(List<StockLineDto> dtos){
        List<List<Object>> dataList = new ArrayList<>();
        dataList.add(Lists.newArrayList("date", "code", "name", "price"));
        for (StockLineDto dto : dtos){
            List<Object> tmpList = new ArrayList<>();
            tmpList.add(Integer.valueOf(dto.getDate().replace("-", "")));
            tmpList.add(dto.getCode());
            tmpList.add(dto.getName());
            tmpList.add(dto.getPrice());
            dataList.add(tmpList);
        }
        return dataList;

    }



    public static List<StockLineDto> queryKlineData(String code, String start, String end, String klt) {
        if(StrUtil.isBlank(klt)){
            klt = "101";
        }
        if(StrUtil.isBlank(end)){
            end = "20300101";
        }
        if(StrUtil.isBlank(start)){
            start = "20200101";
        }
        String url = server + "?" + composeParams(code, start, end, klt);

        String body = HttpUtil.get(url);
        JSONObject jsonObject = JSONObject.parseObject(body);
        JSONObject data = jsonObject.getJSONObject("data");
        String name = data.getString("name");
        JSONArray arrays = data.getJSONArray("klines");
        String string = arrays.getString(0);
        // 基准价格
        BigDecimal base = new BigDecimal(string.split(",")[2]);
        List<StockLineDto> dtoList = new ArrayList<>();
        for (int i = 0; i < arrays.size(); i++) {
            String ele = arrays.getString(i);
            String[] split = ele.split(",");
            StockLineDto dto = new StockLineDto();
            dto.setDate(split[0]);
            dto.setCode(code);
            dto.setName(StrUtil.replace(name, " ", ""));
            dto.setRate(new BigDecimal(split[8]));
            dto.setPrice(NumberUtil.mul(BigDecimal.valueOf(1000000),new BigDecimal(split[2])).divide(base, 2, RoundingMode.HALF_UP));
            dtoList.add(dto);
        }

        return dtoList;
    }

    public static void main(String[] args) {
        String code = "002032";
        String start = "20200101";
        String end = "20300101";
        String klt = "101";


        // 美的集团 苏泊尔
        List<StockLineDto> dtos = queryKlineData(code, start, end, klt);
        List<List<Object>> lists = queryKlineList(dtos);
        System.out.println(JSONObject.toJSONString(lists));


    }



    public static String composeParams(String code, String start, String end, String klt) {

        String tmp = "0." + code;
        if (StrUtil.startWith(code, "6") || StrUtil.startWith(code, "5")) {
            tmp = "1." + code;
        }

        Map<String, String> json = new HashMap<>();
        json.put("secid", tmp);
        json.put("ut", "fa5fd1943c7b386f172d6893dbfba10b");
        json.put("fields1", "f1,f2,f3,f4,f5,f6");
        json.put("fields2", "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61");
        json.put("klt", klt);
        json.put("fqt", "1");
        json.put("beg", start);
        json.put("end", end);
        json.put("lmt", "1000000");

        String result = "";
        for (Map.Entry<String, String> entry : json.entrySet()) {
            result += entry.getKey() + "=" + entry.getValue() + "&";
        }
        String sub = StrUtil.sub(result, 0, result.length() - 1);
        return sub;
    }


}
