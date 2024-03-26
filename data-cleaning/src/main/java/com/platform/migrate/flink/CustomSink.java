package com.platform.migrate.flink;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CustomSink extends RichSinkFunction<String> {

    private static final long serialVersionUID = 6358342353769937189L;

    @Override
    public void invoke(String value, Context context) {
        log.info("收到变更原始数据:{}", value);
        JSONObject json = JSON.parseObject(value);
        // 数据操作类型 c 新增数据 u 更新数据  d 删除数据
        String op = json.getString("op");
        JSONObject before = json.getJSONObject("before");
        JSONObject after = json.getJSONObject("after");
        // ts_ms
        JSONObject source = json.getJSONObject("source");
        log.info("information time {} db {} table {}", source.getString("ts_ms"), source.getString("db"), source.getString("table"));
        if ("c".equals(op)) {
            log.info("insert data \n{} \n{}", before, after);
        }
        if ("u".equals(op)) {
            log.info("update data \n{} \n{}", before, after);
        }
        if ("d".equals(op)) {
            log.info("delete data \n{} \n{}", before, after);
        }
    }



}
