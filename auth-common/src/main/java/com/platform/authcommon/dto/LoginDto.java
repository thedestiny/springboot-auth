package com.platform.authcommon.dto;

import lombok.Data;

import java.util.List;

/**
 * 登录对象
 * @Description
 * @Date 2023-09-11 11:17 AM
 */

@Data
public class LoginDto {


    private String userId;
    private String username;
    private List<String> authorities;
    private String jti;
    private Long expireIn;


}
