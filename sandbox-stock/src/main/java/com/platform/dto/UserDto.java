package com.platform.dto;

import com.platform.senstive.SensitiveEnum;
import com.platform.senstive.Sensitivity;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 9201899924447997209L;

    @Sensitivity(strategy = SensitiveEnum.USERNAME)
    private String username;

    @Sensitivity(strategy = SensitiveEnum.PHONE)
    private String cellphone;

    @Sensitivity(strategy = SensitiveEnum.ADDRESS)
    private String address;

    @Sensitivity(strategy = SensitiveEnum.EMAIL)
    private String email;

    private Integer age;


}
