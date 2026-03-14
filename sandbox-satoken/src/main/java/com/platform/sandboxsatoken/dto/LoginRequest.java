package com.platform.sandboxsatoken.dto;

import cn.hutool.core.lang.UUID;
import lombok.Data;

@Data
public class LoginRequest {

    private String username;

    private String password;


    public static void main(String[] args) {


        String string = UUID.fastUUID().toString();
        System.out.println(string);


    }
}
