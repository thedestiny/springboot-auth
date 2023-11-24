package com.platform.flex.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.platform.flex.dto.StudentReq;
import com.platform.flex.entity.Student;

import java.util.List;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-11-14 2:33 PM
 */
public interface StudentMapper extends BaseMapper<Student> {


    List<Student> queryStudentPageList(StudentReq studentReq);

}
