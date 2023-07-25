package com.platform.orderserver.listener;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @Description mysql binlog 监听
 * @Author kaiyang
 * @Date 2023-07-13 10:45 AM
 */
@Slf4j
public class MysqlBinLogListener implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 异步执行任务
        new Thread(this::mysqlBinLog).start();
    }

    /**
     * 打印数据库表信息
     */
    public void printSt(Db use, String sl, String db, String tb, long tabId) {

        try {
            String qu = StrUtil.format(sl, db, tb);
            log.info("qu {}", qu);
            List<Entity> lst = use.query(qu);
            System.out.println(String.format("\n数据库表信息  %s\t%s\t%s", tabId, db, tb));
            String format = String.format("\n%-18s\t%-18s\t%-18s\t%-18s", "字段", "类型", "注释", "sort");
            List<String> dts = new ArrayList<>();
            System.out.println(format);
            for (Entity entity : lst) {
                String name = entity.getStr("name");
                String type = entity.getStr("type");
                String comment = entity.getStr("comment");
                String srt = entity.getStr("srt");
                // %-18s 左对齐 18个字符  %18s 右对齐 18个字符
                format = String.format("%-18s\t%-18s\t%-18s\t%-18s", name, type, comment, srt);
                dts.add(format);
                System.out.println(format);
            }
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {

        MysqlBinLogListener listener = new MysqlBinLogListener();
        listener.mysqlBinLog();


    }


    public void mysqlBinLog() {


        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/account?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // hu-tools 操作数据库
        Db use = Db.use(dataSource, "com.mysql.cj.jdbc.Driver");
        // 查询数据库表结构
        String sl = "SELECT COLUMN_NAME as 'name', DATA_TYPE as 'type', COLUMN_COMMENT as 'comment', ordinal_position as 'srt' \n" +
                "FROM information_schema.`COLUMNS` \n" +
                "WHERE TABLE_SCHEMA = '{}' AND TABLE_NAME = '{}' order by ordinal_position ;";

        Map<Long, String> tabMap = new HashMap<>();
        //自己MySQL的信息。host，port，username，password
        BinaryLogClient client = new BinaryLogClient("localhost", 3306, "root", "123456");
        // 设置监听的server_id 必须是唯一的，该server_id 作为从节点来接受 mysql 数据
        client.setServerId(100);
        client.setKeepAlive(true); // 保持连接
        client.setKeepAliveInterval(10 * 1000); // 心跳包发送频率
        client.setKeepAliveConnectTimeout(5 * 1000); // 心跳发送超时设置

        // https://blog.csdn.net/m0_69424697/article/details/124947861

        client.registerEventListener(event -> {
            log.info("start !");
            EventHeader header = event.getHeader();
            EventType type = header.getEventType();
            log.info("header {}", JSONObject.toJSONString(header));
            // 数据事件 注册事件监听器，对不同类型的事件做出响应。
            EventData data = event.getData();
            // RotateEventData
            if(data instanceof RotateEventData){
                RotateEventData dat0 = (RotateEventData) data;
                System.out.println(dat0.getBinlogFilename() + " == " + dat0.getBinlogPosition());
            }
            // FormatDescriptionEventData
            if(data instanceof FormatDescriptionEventData){
                FormatDescriptionEventData dat = (FormatDescriptionEventData) data;
                System.out.println(dat.getBinlogVersion() + " == " + dat.getDataLength());
            }
            // TableMapEventData
            if (data instanceof TableMapEventData) {
                TableMapEventData tableMapEventData = (TableMapEventData) data;
                long tableId = tableMapEventData.getTableId();
                // 监听到的数据库表和数据库
                String tableName = tableMapEventData.getTable();
                String database = tableMapEventData.getDatabase();
                tabMap.put(tableId, StrUtil.format("{}=={}", database, tableName));
                printSt(use, sl, database, tableName, tableId);
                // log.info("database {}  tableId {} tableName {}", database, tableId, tableName);
            }
            // 对UpdateRowsEventData、WriteRowsEventData、DeleteRowsEventData类型的事件进行输出日志
            // 数据 保存 更新 删除
            if (data instanceof WriteRowsEventData) {
                WriteRowsEventData dat1 = (WriteRowsEventData) data;
                long tableId = dat1.getTableId();
                String s = tabMap.get(tableId);
                String db = s.split("==")[0];
                String tab = s.split("==")[1];
                BitSet columns = dat1.getIncludedColumns();
                // log.info("id {} db {} tb {}", tableId, db, tab);
                System.out.println(String.format("\n数据库表信息  %s\t%s\t%s", tableId, db, tab));
                System.out.println("\ninsert columns \n" + columns.toString() + "\n");
                // log.info("insert tableId {} data {}", tableId, data.toString());
                List<Serializable[]> rows = dat1.getRows();
                for (Serializable[] row : rows) {
                    System.out.println("data " + Arrays.toString(row));
                }

            }
            // 数据更新
            if (EventType.isUpdate(type)) {
                UpdateRowsEventData dat2 = (UpdateRowsEventData) data;
                long tableId = dat2.getTableId();
                String s = tabMap.get(tableId);
                String db = s.split("==")[0];
                String tab = s.split("==")[1];
                BitSet columns = dat2.getIncludedColumns();
                // log.info("id {} db {} tb {}", tableId, db, tab);
                System.out.println(String.format("\n数据库表信息  %s\t%s\t%s", tableId, db, tab));
                // log.info("columns {}", columns.toString());
                System.out.println("\nupdate columns \n" + columns.toString() + "\n");
                List<Map.Entry<Serializable[], Serializable[]>> rows = dat2.getRows();
                for (Map.Entry<Serializable[], Serializable[]> row : rows) {
                    Serializable[] key = row.getKey();
                    Serializable[] value = row.getValue();
                    System.out.println("before -->\n" + Arrays.toString(key));
                    System.out.println("after  -->\n" + Arrays.toString(value));
                }
            }
            // 数据删除
            if (data instanceof DeleteRowsEventData) {
                DeleteRowsEventData dat3 = ((DeleteRowsEventData) data);
                BitSet columns = dat3.getIncludedColumns();
                List<Serializable[]> rows = dat3.getRows();

                long tableId = dat3.getTableId();
                String s = tabMap.get(tableId);
                String db = s.split("==")[0];
                String tab = s.split("==")[1];
                // log.info("delete tableId {} data {}", tableId, data.toString());
                System.out.println(String.format("\n数据库表信息  %s\t%s\t%s", tableId, db, tab));
                System.out.println("\ndelete columns \n" + columns.toString() + "\n");
                for (Serializable[] row : rows) {
                    System.out.println("data " + Arrays.toString(row));
                }
            }
        });

        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
