package com.platform.flex;

import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.row.Db;
import com.platform.flex.entity.Student;
import com.platform.flex.mapper.StudentMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description
 * @Date 2023-11-14 11:01 AM
 */
@Slf4j
@EnableAsync
@EnableScheduling
@SpringBootApplication
@SpringBootConfiguration
@EnableConfigurationProperties
@MapperScan(basePackages = "com.platform.flex.mapper")
public class FlexApplication {

    public static void main(String[] args) {

        log.info("start product flex ! ");
        SpringApplication.run(FlexApplication.class, args);


        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/account01?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true&useAffectedRows=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        MybatisFlexBootstrap.getInstance().setDataSource(dataSource).addMapper(StudentMapper.class)
                .start();






        //示例1：查询 id=100 条数据
        // Student student = MybatisFlexBootstrap.getInstance().

//                .execute(StudentMapper.class, mapper ->
//                        mapper.selectOneByEntityId(100);
//                );

    }


}
