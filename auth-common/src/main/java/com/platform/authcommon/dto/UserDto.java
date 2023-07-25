package com.platform.authcommon.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * Created by macro on 2020/6/19.
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class UserDto implements Serializable {

    private Long id;
    private String username;
    private String password;
    private Integer status;
    private String jti;
    private String client_id;
    // 用户角色
    private List<String> roles;
    private List<String> aud;

    private List<String> scope;
    // 用户权限
    private List<String> authorities;

}
