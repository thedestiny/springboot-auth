package com.platform.dataflink.utils;


import com.platform.dataflink.dto.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.connector.kafka.source.reader.deserializer.KafkaRecordDeserializationSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.*;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.SlidingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public class FlinkDataStreamUtils {

    private final static String[] WORDS = {"qwe dfg", "345", "567", "234"};

    public static void main(String[] args) throws Exception {

        // https://www.kancloud.cn/zhangpn/flink/1743689 flink 文档


        // https://blog.csdn.net/weixin_35516273/article/details/154407046

        // flink https://zhuanlan.zhihu.com/p/1953771119237653732
        // map 映射
        // filter 过滤数据
        // flatmap 数据打平 扁平映射
        // keyBy 分组
        // reduce 聚合数据
        // aggregate 自定义聚合


        ParameterTool parameterTool = ParameterTool.fromArgs(args);
        // 创建执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(10_000); // 每10s做一次检查
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);


        env.getCheckpointConfig().setCheckpointTimeout(60_000); // 检查点超时时间 60s
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(50_000); // 检查点之间最小暂停时间
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1); // 最大并发检查点数量

        env.setParallelism(9); // 设置并发度

        List<UserInfoDto> dtoList = new ArrayList<>();
        dtoList.add(new UserInfoDto("1","小张",10, new Date(433), new Date()));
        dtoList.add(new UserInfoDto("2","小王",11, new Date(333), new Date()));
        dtoList.add(new UserInfoDto("3","小李",12, new Date(444), new Date()));

        DataStream<UserInfoDto> listData = env.fromElements(
                new UserInfoDto("1","小张",10, new Date(433), new Date(3)),
                new UserInfoDto("1","小张",10, new Date(433), new Date(4)),
                new UserInfoDto("1","小张",10, new Date(433), new Date(5)),
                new UserInfoDto("1","小张",10, new Date(433), new Date(1))
        );
        // 指定 watermark 时间并指定 eventTime 时间为 applyTime
        // data-warehouse-learning
        DataStream<UserInfoDto> tmpList = listData
                .assignTimestampsAndWatermarks(WatermarkStrategy.<UserInfoDto>forBoundedOutOfOrderness(Duration.ofSeconds(1))
                        .withTimestampAssigner((node,tms) -> node.getApplyTime().getTime()));
        // 根据用户名汇总
        SingleOutputStreamOperator<Tuple2<String, Integer>> process = tmpList.keyBy(UserInfoDto::getUsername)
                .window(TumblingEventTimeWindows.of(Time.seconds(1)))
                .process(new ProcessWindowFunction<UserInfoDto, Tuple2<String, Integer>, String, TimeWindow>() {
                    private static final long serialVersionUID = 7936463436978621626L;

                    @Override
                    public void process(String username, ProcessWindowFunction<UserInfoDto, Tuple2<String, Integer>, String, TimeWindow>.Context context,
                                        Iterable<UserInfoDto> elements, Collector<Tuple2<String, Integer>> out) throws Exception {
                        int cnt = 0;
                        for (UserInfoDto element : elements) {
                            cnt++;
                        }
                        out.collect(new Tuple2<>(username, cnt));
                    }
                });
        // 汇总每个用户某个窗口内的数据量
        process.print();


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
        // 窗口分为三种 滚动窗口 滑动窗口 和 session 窗口以及 global全局窗口
        // 窗口语义 tumbling sliding session global
        // 时间语义 event time/ processing time /ingestion time

        // 滚动窗口 event-time 窗口, 窗口第一个参数为窗口长度，第二个窗口的偏移量，不设置偏移量时为偏移时间
        WindowedStream<UserInfoDto, String, TimeWindow> window1 = stream.window(TumblingEventTimeWindows.of(Time.minutes(2)));
        // 滚动 processing-time 窗口
        WindowedStream<UserInfoDto, String, TimeWindow> window2 = stream.window(TumblingProcessingTimeWindows.of(Time.minutes(2)));
        // 长度为1天的滚动 event-time 窗口,偏移量为 -8小时
        WindowedStream<UserInfoDto, String, TimeWindow> window3 = stream.window(TumblingEventTimeWindows.of(Time.days(1), Time.hours(-8)));

        // 滑动窗口 滑动 event-time 窗口
        WindowedStream<UserInfoDto, String, TimeWindow> window4 = stream.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)));
        // 滑动窗口 滑动 processing-time 窗口
        WindowedStream<UserInfoDto, String, TimeWindow> window5 = stream.window(SlidingProcessingTimeWindows.of(Time.seconds(10), Time.seconds(5)));
        // 滑动窗口 第一个参数为窗口时间 第二个是滑动距离 第三个是设置偏移量
        WindowedStream<UserInfoDto, String, TimeWindow> window6 = stream.window(SlidingProcessingTimeWindows.of(Time.hours(10), Time.hours(1), Time.minutes(30)));





        stream.print();
        env.execute("flink stream");





    }


    public void sourceStream(){
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 统一 API 示例：DataStream API 支持有界与无界流 new FlinkKafkaConsumer.<>(null)
        DataStream<String> stream = env.addSource(null); // 无界流
        DataStream<String> batchStream = env.readTextFile("hdfs://path/to/file"); // 有界流，仍为 DataStream

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
