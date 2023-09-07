package com.platform.productserver.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 用户信息
 */
@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = -5523990557643918662L;

    private Long id;

    private String username;

    private String password;

    private List<String> roles;

    private BigDecimal weight;


}
