package com.platform.authserver.domain;

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
public class UserDTO implements Serializable {

    private Long id;
    private String username;
    private String password;
    private Integer status;
    private List<String> roles;

}
