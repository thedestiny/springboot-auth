package com.platform.flex.service;

import com.mybatisflex.core.service.IService;
import com.platform.flex.entity.Student;

/**
 * @Description
 * @Date 2023-11-14 2:34 PM
 */
public interface StudentService extends IService<Student> {



    Student queryEntityById(Long id);


}
