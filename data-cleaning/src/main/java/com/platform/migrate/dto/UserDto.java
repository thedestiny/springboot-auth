package com.platform.migrate.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description
 * @Author kaiyang
 * @Date 2024-03-27 5:25 PM
 */

@Data
public class UserDto implements Serializable {


    private String username;

    private String password;




}
