package com.platform.flex.web;

import com.alibaba.fastjson.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import com.platform.flex.entity.Student;
import com.platform.flex.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

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
    public String test(){
        

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

        query.setAddress("河南省郑州市中原区");
        studentService.updateById(query);
        log.info("student is {}", query);

        return "ss";
    }

    @DeleteMapping(value = "/batch/{ids}")
    public String index(@PathVariable Long[] ids) {
        log.info("ids is {}", JSONObject.toJSONString(ids));
        return "success";
    }


}
