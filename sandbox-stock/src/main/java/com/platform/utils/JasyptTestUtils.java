package com.platform.utils;


import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.BasicTextEncryptor;

@Slf4j
public class JasyptTestUtils {


    public static void main(String[] args) {


        // 创建 BasicTextEncryptor 对象进行简单的加密解密操作
        BasicTextEncryptor encryptor = new BasicTextEncryptor();
        // 设置加密密钥，与应用程序中使用的密钥一致
        encryptor.setPassword("sec123456");
        String encrypt = encryptor.encrypt("1234567");
        // 加密密码
        System.out.println("加密内容 " + encrypt);

        String decrypt = encryptor.decrypt(encrypt);
        System.out.println("解密内容 " + decrypt);


    }

}
