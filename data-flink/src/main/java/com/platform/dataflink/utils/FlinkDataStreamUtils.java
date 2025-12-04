package com.platform.dataflink.utils;


import com.platform.dataflink.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class FlinkDataStreamUtils {

    private final static String[] WORDS = {"qwe dfg", "345", "567", "234"};

    public static void main(String[] args) throws Exception {

        // https://www.kancloud.cn/zhangpn/flink/1743689 flink 文档


        // flink https://zhuanlan.zhihu.com/p/1953771119237653732
        // map 映射
        // filter 过滤数据
        // flatmap 数据打平 扁平映射
        // keyBy 分组
        // reduce 聚合数据
        // aggregate 自定义聚合


        ParameterTool parameterTool = ParameterTool.fromArgs(args);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10_000); // 每10s做一次检查
        env.getCheckpointConfig().setCheckpointTimeout(60_000); // 检查点超时时间 60s
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(50_000); // 检查点之间最小暂停时间
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1); // 最大并发检查点数量

        env.setParallelism(9); // 设置并发度

        env.getConfig().setGlobalJobParameters(parameterTool);

        DataStreamSource<String> dataStreamSource = env.socketTextStream("hostname", 7654);
        DataStream<UserInfoDto> map = dataStreamSource.map(new MapFunction<String, UserInfoDto>() {
            @Override
            public UserInfoDto map(String s) throws Exception {
                UserInfoDto dto = new UserInfoDto();
                return dto;
            }
        });

        KeyedStream<UserInfoDto, String> stream = map.keyBy(new KeySelector<UserInfoDto, String>() {
            @Override
            public String getKey(UserInfoDto userInfoDto) throws Exception {
                return userInfoDto.getId();
            }
        });

        WindowedStream<UserInfoDto, String, TimeWindow> window = stream.window(TumblingEventTimeWindows.of(Time.minutes(2)));



        stream.print();
        env.execute("flink stream");





    }

    public void kafkaConfig(StreamExecutionEnvironment env){

        // 2. 配置Kafka参数
        String kafkaBootstrapServers = "172.30.244.152:9092";
        String topic = "app_logs";
        String consumerGroup = "flink-log-analysis";

        // 3. 定义Kafka Source
        KafkaSource<String> kafkaSource = KafkaSource.<String>builder()
                .setBootstrapServers(kafkaBootstrapServers)
                .setTopics(topic)
                .setGroupId(consumerGroup)
                .setDeserializer(new KafkaRecordDeserializationSchema<String>() {
                    @Override
                    public void deserialize(ConsumerRecord<byte[], byte[]> record, Collector<String> out) throws IOException {
                        String value = new String(record.value(), StandardCharsets.UTF_8);
                        out.collect(value);
                    }

                    @Override
                    public TypeInformation<String> getProducedType() {
                        return TypeInformation.of(String.class);
                    }
                })
                .setStartingOffsets(OffsetsInitializer.earliest())
                // 添加Kafka客户端属性以提高稳定性
                .setProperty("enable.auto.commit", "false") // 由Flink管理偏移量提交
                .setProperty("session.timeout.ms", "45000")
                .setProperty("max.poll.interval.ms", "300000")
                .setProperty("heartbeat.interval.ms", "10000")
                .setProperty("retry.backoff.ms", "1000")
                .setProperty("reconnect.backoff.max.ms", "10000")
                .setProperty("reconnect.backoff.ms", "1000")
                .build();


        // 4. 从Kafka读取数据
        DataStream<String> logStream = env.fromSource(
                kafkaSource,
                WatermarkStrategy.noWatermarks(),
                "Kafka Log Source"
        );
    }

}
