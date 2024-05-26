package com.platform.utils;

import com.github.javafaker.Faker;

import java.util.Locale;

public class FakerTest {

    public static void main(String[] args) {


        // Locale.CHINA
        // new Locale("zh", "CN")
        Faker faker = new Faker(Locale.CHINA);

        for (int i = 0; i < 1; i++) {

            // 用户名称
            String username = faker.name().fullName();
            System.out.println("username  " + username);
            // 随机数
            Integer num = faker.random().nextInt(10, 1000);
            System.out.println("num  " + num);
            // 地址
            String address = faker.address().fullAddress();
            System.out.println("address  " + address);
            // 邮箱
            String email = faker.internet().emailAddress(String.valueOf(num));
            System.out.println("email  " + email);
            // 手机号
            String cellphone = faker.phoneNumber().phoneNumber();
            System.out.println("cellphone  " + cellphone);
            // 日期
            String birthday = faker.date().birthday().toString();
            System.out.println("birthday  " + birthday);
            // System.out.println(s);

        }



    }
}
