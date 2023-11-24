package com.platform.flex.service.impl;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.platform.flex.dto.StudentReq;
import com.platform.flex.entity.Student;
import com.platform.flex.mapper.StudentMapper;
import com.platform.flex.service.StudentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-11-14 2:34 PM
 */

@Slf4j
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements Serializable, StudentService {

    private static final long serialVersionUID = -5684769786023519996L;

    @Override
    public Student queryEntityById(Long id) {

        StudentReq req = new StudentReq();
        req.setUsername("晓");
        // 只是做增强, 全面的还是使用 mybatis-plus
        QueryWrapper wrapper = new QueryWrapper();
        Student student = mapper.selectOneById(id);
        // Student student = mapper.select(id);
        log.info("student is {}", student);
        return student;
    }


}
