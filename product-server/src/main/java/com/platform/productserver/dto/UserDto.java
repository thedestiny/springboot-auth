package com.platform.productserver.dto;

import lombok.Data;

import java.util.List;

/**
 * 用户信息
 */
@Data
public class UserDto {
    private Long id;
    private String username;
    private String password;
    private List<String> roles;
}
