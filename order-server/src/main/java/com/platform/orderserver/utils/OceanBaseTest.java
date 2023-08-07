package com.platform.orderserver.utils;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * oceanbase 数据库连接
 */
@Slf4j
public class OceanBaseTest {


    public static void main(String[] args) throws Exception {

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://obmti0vk70rfoej0-mi.oceanbase.aliyuncs.com:3306/treasure?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true");
        dataSource.setUsername("username");
        dataSource.setPassword("password!");
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        // obmti0vk70rfoej0-mi.oceanbase.aliyuncs.com:3306
        // hu-tools 操作数据库
        Db use = Db.use(dataSource, "com.mysql.cj.jdbc.Driver");
        List<Entity> datas = use.query("select * from tb_stock_info");
        for (Entity data : datas) {
            log.info("entity {}", JSONObject.toJSONString(data));
        }


    }
}
