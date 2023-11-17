package com.platform.flex.utils;

/**
 * @Description
 * @Date 2023-11-15 2:38 PM
 */
public class AppUtils {

    private String name;
    private Integer age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    private AppUtils() {
    }

    public static AppUtils newApp(String name, Integer age){

        AppUtils appUtils = new AppUtils();
        appUtils.setAge(age);
        appUtils.setName(name);

        return appUtils;
    }
}
