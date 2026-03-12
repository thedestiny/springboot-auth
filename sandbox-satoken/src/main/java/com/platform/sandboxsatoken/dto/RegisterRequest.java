package com.platform.sandboxsatoken.dto;

import lombok.Data;

@Data
public class RegisterRequest {

    private String username;

    private String password;

    private String nickname;
}
