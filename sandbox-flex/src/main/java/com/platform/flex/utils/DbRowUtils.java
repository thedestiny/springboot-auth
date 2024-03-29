package com.platform.flex.utils;

import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.MybatisFlexBootstrap;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.row.Row;
import com.mybatisflex.core.row.RowKey;
import com.mybatisflex.core.row.RowUtil;
import com.platform.flex.dto.StudentDto;
import com.platform.flex.entity.Student;
import com.platform.flex.entity.table.AccountTableDef;
import com.platform.flex.mapper.StudentMapper;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.platform.flex.entity.table.AccountTableDef.ACCOUNT;
import static com.platform.flex.entity.table.StudentTableDef.STUDENT;

/**
 *
 * Db + row
 * @Description
 * @Date 2023-11-24 11:16 AM
 */

@Slf4j
public class DbRowUtils {

    public static void main(String[] args) {


        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://127.0.0.1:3306/account01?useUnicode=true&characterEncoding=utf8&useSSL=false&tinyInt1isBit=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true&rewriteBatchedStatements=true&useAffectedRows=true&allowPublicKeyRetrieval=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");

        MybatisFlexBootstrap.getInstance().setDataSource(dataSource).addMapper(StudentMapper.class)
                .start();
        RowKey myRowKey = RowKey.of("id", KeyType.Auto, "id", true);
        Row account = new Row();
        account.set(ACCOUNT.ID, 1001);
        account.set(ACCOUNT.USER_ID, "234");
        Db.insert("tb_account", account);

        // 数据单条查询
        Row row = Db.selectOneById("tb_student", "id", 100);
        Student stu = row.toEntity(Student.class);
        log.info("student is {}", stu);
        // 数据批量查询
        String listSql = "select * from tb_student where age > ?";
        List<Row> rows = Db.selectListBySql(listSql, 2);
        // 通过 RowUtil.toEntityList 转换数据对象
        List<StudentDto> studentDtos = RowUtil.toEntityList(rows, StudentDto.class);
        log.info("row is {}", rows);
        // 数据分页查询
        QueryWrapper query = QueryWrapper.create()
                .where(STUDENT.AGE.ge(18));
        Page<Row> pages = Db.paginate("tb_student", 1, 10, query);
        // 数据的保存
        String sql = "insert into tb_account(id, user_id) value (?, ?)";
        Db.insertBySql(sql,100,"12");


    }

}
