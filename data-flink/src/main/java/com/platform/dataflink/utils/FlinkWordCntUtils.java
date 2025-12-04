package com.platform.dataflink.utils;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

@Slf4j
public class FlinkWordCntUtils {

    public static void main(String[] args) throws Exception {


        // ParameterUtil
        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        env.getConfig().setGlobalJobParameters(parameterTool);
        // 间隔 20秒进行存储
//        CheckpointConfig config = env.getCheckpointConfig();
//        config.setCheckpointInterval(20_000);
//        config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
//        // 开启 checkpointing 每秒触发一次检查点
//        env.enableCheckpointing(1_000);

        // 读取数据
        // flatmap 数据打平
        String[] words = {"asd 444", "444", "45", "444", "asd"};
        // 按照单次分组并统计
        env
                .fromElements(words)
                .flatMap((FlatMapFunction<String, Tuple2<String, Integer>>) (element, collector) -> {
                    // 数据打平
                    String[] arr = element.toLowerCase().split("\\W+");
                    log.info("flat map node {}", JSONObject.toJSONString(arr));
                    for (String s1 : arr) {
                        if (s1.length() > 0) {
                            collector.collect(new Tuple2<>(s1, 1));
                        }
                    }
                })
                .groupBy(0)
                .reduce((ReduceFunction<Tuple2<String, Integer>>) (val1, val2) -> new Tuple2<>(val1.f0, val1.f1 + val2.f1))
                .print();
        // 分组并reduce

        // DataStreamSource<String> stream =

    }


}
