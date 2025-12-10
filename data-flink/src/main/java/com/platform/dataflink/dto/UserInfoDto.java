package com.platform.dataflink.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDto {

    private String id;

    private String username;

    private Integer age;

    private Date applyTime;


    private Date createTime;




}
