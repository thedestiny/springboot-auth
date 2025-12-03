package com.platform.migrate.flink;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.IterativeStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;


/**
 * @Description
 * @Author liangkaiyang
 * @Date 2025-12-02 11:00 AM
 */


@Slf4j
public class FlinkTest001 {

    public static void main(String[] args) throws Exception {

        // ParameterUtil


        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // env.getConfig().setGlobalJobParameters();
        // 间隔 20秒进行存储
        CheckpointConfig config = env.getCheckpointConfig();
        config.setCheckpointInterval(20_000);
        config.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        // 开启 checkpointing 每秒触发一次检查点
        env.enableCheckpointing(1_000);

        // 读取数据
        String words = "34 45 23";
        // 按照单次分组并统计
        env.fromElements(words)
                .flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
                    @Override
                    public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
                        String[] arr = s.toLowerCase().split(" ");
                        for (String s1 : arr) {
                            if (s1.length() > 0) {
                                collector.collect(new Tuple2<>(s1, 1));
                            }
                        }
                    }
                }).keyBy(0)
                .reduce((ReduceFunction<Tuple2<String, Integer>>) (val1, val2) -> new Tuple2<>(val1.f0, val1.f1 + val2.f1))
                .print();
        // 分组并reduce

        DataStreamSource<String> stream = env.socketTextStream("hostname", 2205);
        // stream.flatMap().


        DataStream<Long> streamSources = env.generateSequence(0, 10000);
        IterativeStream<Long> iterate = streamSources.iterate(1000);

        SingleOutputStreamOperator<Long> map = iterate.map(new MapFunction<Long, Long>() {
            @Override
            public Long map(Long value) throws Exception {
                return value * 2;
            }
        });
        // 进行格式转换
        SingleOutputStreamOperator<String> map1 = iterate.map(node -> String.valueOf(2 * node));
        // 进行格式转换
        env.socketTextStream("localhost", 9999)
                .flatMap(new Spliterator())
                .keyBy(0)
                // .timeWindow(Time.seconds(5))
                .countWindow(200)
                .sum(1);

        env.execute("FlinkTest001");


    }

    // 进行格式转换
    static class Spliterator implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
            String[] words = value.split("\\s+");
            for (String word : words) {
                out.collect(new Tuple2<>(word, 1));
            }
        }
    }


}
