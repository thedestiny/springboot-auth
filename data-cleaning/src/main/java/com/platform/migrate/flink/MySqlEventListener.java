package com.platform.migrate.flink;


import lombok.extern.slf4j.Slf4j;

import com.ververica.cdc.connectors.mysql.source.MySqlSource;
import com.ververica.cdc.connectors.mysql.table.StartupOptions;
import com.ververica.cdc.debezium.JsonDebeziumDeserializationSchema;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class MySqlEventListener  implements CommandLineRunner {

// https://mp.weixin.qq.com/s/cTQJT2Cye8JBg_GIuaP8gw

    @Autowired
    private CustomSink customSink;

    @Override
    public void run(String... args) throws Exception {
        MySqlSource<String> mySqlSource = MySqlSource.<String>builder()
                .hostname("127.0.0.1")
                .port(3306)
                // 设置捕获的数据库，如果需要同步整个数据库，请将 tableList 设置为 ".*".
                .databaseList("student")
                // 设置捕获的表
                .tableList("student.student_userinfo")
                .username("root")
                .password("123456")
                // 将 SourceRecord 转换为 JSON 字符串
                .deserializer(new JsonDebeziumDeserializationSchema())
                // latest:只进行增量导入(不读取历史变化) （默认）initial初始化快照,即全量导入后增量导入(检测更新数据写入)
                .startupOptions(StartupOptions.latest())
                // 设置时区
                .serverTimeZone("Asia/Shanghai")
                .build();

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 设置 3s 的 checkpoint 间隔
        env.enableCheckpointing(3000);
        DataStreamSource<String> streamSource = env.fromSource(mySqlSource, WatermarkStrategy.noWatermarks(), "MySQL Source")
                // 设置 source 节点的并行度为 1
                .setParallelism(1);

        // 设置 sink 节点并行度为 1
        streamSource.addSink(customSink).setParallelism(1);
        env.execute("Print MySQL Snapshot + Binlog");
    }



}
