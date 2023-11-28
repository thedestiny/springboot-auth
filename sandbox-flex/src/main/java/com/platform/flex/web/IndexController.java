package com.platform.flex.web;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.row.Db;
import com.platform.flex.dto.StudentDto;
import com.platform.flex.entity.Student;
import com.platform.flex.entity.table.AccountTableDef.*;
import com.platform.flex.entity.table.StudentTableDef.*;
import com.platform.flex.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.mybatisflex.core.query.QueryMethods.*;
import static com.platform.flex.entity.table.StudentTableDef.STUDENT;
import static com.platform.flex.entity.table.AccountTableDef.ACCOUNT;

/**
 * @Description
 * @Date 2023-11-14 2:11 PM
 */


@Slf4j
@RestController
@RequestMapping(value = "api")
public class IndexController {

    @Autowired
    private StudentService studentService;

    // localhost:8097/api/test
    @GetMapping(value = "test")
    public Page<StudentDto> test() {


        Student student = new Student();
        student.setAge(34);
        student.setWeight(BigDecimal.valueOf(2));
        student.setBirthday(new Date());
        student.setAddress("河南省郑州市");
        student.setUsername("李晓明");
        student.setPhone("13849784423");
        student.setIdCard("420186199512014023");
        // 数据保存和数据更新
        studentService.save(student);
        // 由于设置了脱敏字段，查询出来的对象是脱敏的
        Student query = studentService.queryEntityById(12L);
        if (ObjectUtil.isNotEmpty(query)) {
            query.setAddress("河南省郑州市中原区");
            studentService.updateById(query);
            log.info("student is {}", query);
        }

        List<String> list = Lists.newArrayList();
        QueryWrapper wrapper = new QueryWrapper();
        // 数据查询
        // wrapper.select(STUDENT.AGE, STUDENT.ADDRESS);
        // select * from tb_student where (age > 23 and address like "%郑州%") and (id > 0 or id_card in ())
        wrapper.select(STUDENT.ALL_COLUMNS)
                .from(STUDENT)
                .where(STUDENT.AGE.ge(23).and(STUDENT.ADDRESS.like("郑州"))).and(STUDENT.ID.ge(0).or(STUDENT.ID_CARD.in(list)))
                .orderBy(STUDENT.ID.desc());

        List<StudentDto> dtos = studentService.listAs(wrapper, StudentDto.class);
        log.info("列表查询数据 {}", JSONObject.toJSONString(dtos));
        // 单表分页查询
        Page<StudentDto> pages = studentService.pageAs(Page.of(1, 10), wrapper, StudentDto.class);
        Page<Student> page1 = studentService.page(Page.of(1, 10), wrapper);
        log.info("pages is {}", JSONObject.toJSONString(page1, SerializerFeature.PrettyFormat));

        // 数据关联查询
        // select * from tb_student ts inner join tb_account ta on ta.id = ts.id where ts.id > 1 order by ts.id desc
        QueryWrapper wrapp = QueryWrapper.create()
                .select(ACCOUNT.ALL_COLUMNS)
                .select(STUDENT.ALL_COLUMNS)
                .from(STUDENT)
                .innerJoin(ACCOUNT).on(STUDENT.ID.eq(ACCOUNT.ID))
                .where(STUDENT.ID.ge(1)).orderBy(STUDENT.ID.desc());
        // 关联分页查询使用别名的方式
        Page<StudentDto> page = studentService.pageAs(Page.of(1, 10), wrapp, StudentDto.class);
        log.info("pages is {}", JSONObject.toJSONString(page, SerializerFeature.PrettyFormat));
        // 统计条件
        QueryWrapper wap = QueryWrapper.create()
                .select(STUDENT.ALL_COLUMNS,
                        max(STUDENT.AGE).as("maxAge"),
                        min(STUDENT.AGE).as("minAge"),
                        count().as("cnt"),
                        avg(STUDENT.WEIGHT).as("avgWeight"),
                        sum(STUDENT.WEIGHT).as("sumWeight")
                )
                .from(STUDENT).where(STUDENT.ID.ge(10)).groupBy(STUDENT.ADDRESS);


        return pages;
    }

    @DeleteMapping(value = "/batch/{ids}")
    public String index(@PathVariable Long[] ids) {
        log.info("ids is {}", JSONObject.toJSONString(ids));
        return "success";
    }


}
