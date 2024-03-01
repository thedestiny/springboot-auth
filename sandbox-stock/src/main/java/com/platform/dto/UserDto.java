package com.platform.dto;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.DesensitizedUtil;
import com.platform.senstive.PhoneSensitivity;
import com.platform.senstive.SensitiveEnum;
import com.platform.senstive.Sensitivity;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 9201899924447997209L;

    // 用户名
    @Sensitivity(strategy = SensitiveEnum.USERNAME)
    private String username;
    // 手机号
    @Sensitivity(strategy = SensitiveEnum.PHONE)
    private String cellphone;

    @Sensitivity(strategy = SensitiveEnum.ADDRESS)
    private String address;

    @Sensitivity(strategy = SensitiveEnum.EMAIL)
    private String email;
    private Integer age;


    public static void main(String[] args) {

        String address = NetUtil.getLocalhost().getHostAddress();
        System.out.println(address);

        DesensitizedUtil.userId();

        String sss = DesensitizedUtil.address("sss", 5);




    }
}
