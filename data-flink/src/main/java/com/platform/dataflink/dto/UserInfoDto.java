package com.platform.dataflink.dto;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.event.EventListener;
import org.springframework.format.annotation.DateTimeFormat;
import sun.misc.Unsafe;

import java.util.Date;
import java.util.concurrent.locks.LockSupport;

// implements ApplicationListener
@Data
//@AllArgsConstructor
//@NoArgsConstructor
public class UserInfoDto {

    @EventListener
    public void test(){

    }

    private String id;

    private String username;

    private Integer age;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss", label = "dddd")
    private Date applyTime;

    private String address;

    private Date createTime;

    private String remark;

    public UserInfoDto(String id, String username, Integer age, Date applyTime, Date createTime) {
        this.id = id;
        this.username = username;
        this.age = age;
        this.applyTime = applyTime;
        this.createTime = createTime;
    }

    public UserInfoDto() {
    }

    public static void main(String[] args) {


//        Unsafe unsafe = Unsafe.getUnsafe();
//        unsafe.park();
        UserInfoDto dto = new UserInfoDto();
        dto.setId("33333");
        dto.setUsername("韦孝宽");
        dto.setAge(34);
        dto.setAddress("山西玉壁城");
        dto.setRemark("备注");
        dto.setApplyTime(new Date());
        dto.setCreateTime(new Date());

        // 序列化字符串
        String body = JSONObject.toJSONString(dto, SerializerFeature.PrettyFormat, SerializerFeature.MapSortField);
        String body1 = JSONObject.toJSONString(dto, SerializerFeature.MapSortField);
        System.out.println(body);
        System.out.println(body1);


    }

}
