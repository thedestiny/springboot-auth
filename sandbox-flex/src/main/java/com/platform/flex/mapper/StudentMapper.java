package com.platform.flex.mapper;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.paginate.Page;
import com.platform.flex.entity.Student;

/**
 * @Description
 * @Author kaiyang
 * @Date 2023-11-14 2:33 PM
 */
public interface StudentMapper extends BaseMapper<Student> {


    Page<Student> queryStudentPageList();

}
