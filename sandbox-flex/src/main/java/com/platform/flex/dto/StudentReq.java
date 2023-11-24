package com.platform.flex.dto;

import com.mybatisflex.core.paginate.Page;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Date 2023-11-24 2:47 PM
 */

@Data
public class StudentReq extends Page implements Serializable {

    private static final long serialVersionUID = 7994204994180877087L;

    private Integer age;

    private String username;




}
