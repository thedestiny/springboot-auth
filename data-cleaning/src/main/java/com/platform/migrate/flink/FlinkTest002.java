package com.platform.migrate.flink;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-11 5:23 PM
 */
public class FlinkTest002 {

    private static final String data = "";
    public static void main(String[] args) {


        JSONObject jsonObject = JSONObject.parseObject(data);
        JSONArray rows = jsonObject.getJSONArray("rows");
        // System.out.println(rows);

        int len = rows.size();
         for(int i = 0;  i < len; i++){
             JSONObject row = rows.getJSONObject(i);
             System.out.println(row.getString("sqllog"));
         }


    }
}
