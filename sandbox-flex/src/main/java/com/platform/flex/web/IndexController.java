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
import com.platform.flex.entity.table.StudentTableDef.*;
import com.platform.flex.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.platform.flex.entity.table.StudentTableDef.STUDENT;

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
        studentService.save(student);

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
        wrapper.select(STUDENT.ALL_COLUMNS)
                .from(STUDENT)
                .where(STUDENT.AGE.ge(23).and(STUDENT.ADDRESS.like("郑州"))).and(STUDENT.ID.ge(0).or(STUDENT.ID_CARD.in(list)))
                .orderBy(STUDENT.ID.desc());


//        wrapper.where();
//        wrapper.where();

        String sql = "insert into tb_account(id, user_id) value (?, ?)";
        Db.insertBySql(sql,100,"12");


        Page<StudentDto> pages = studentService.pageAs(Page.of(1, 10), wrapper, StudentDto.class);
        Page<Student> page1 = studentService.page(Page.of(1, 10), wrapper);
        log.info("pages is {}", JSONObject.toJSONString(page1, SerializerFeature.PrettyFormat));

        return pages;
    }

    @DeleteMapping(value = "/batch/{ids}")
    public String index(@PathVariable Long[] ids) {
        log.info("ids is {}", JSONObject.toJSONString(ids));
        return "success";
    }


}
