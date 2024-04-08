package com.platform.dto;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.DesensitizedUtil;
import com.google.protobuf.InvalidProtocolBufferException;
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


    public static void main(String[] args) throws InvalidProtocolBufferException {

        String address = NetUtil.getLocalhost().getHostAddress();
        System.out.println(address);

        Long lg = DesensitizedUtil.userId();
        System.out.println(lg);


        String sss = DesensitizedUtil.address("sss", 5);


        // build 构建对象
        Person.student.Builder builder = Person.student.newBuilder();
        builder.setAddress("河南省洛阳市龙门区23号");
        builder.setFlag(true);
        builder.setName("韦孝宽");
        builder.setDirect(Person.Direction.DOWN);
        builder.setId(345);
        builder.setSeq(123445567890L);

        // 构建对象
        Person.student build = builder.build();

        // 序列化
        byte[] bytes = build.toByteArray();
        // 反序列化
        Person.student student = Person.student.parseFrom(bytes);
        System.out.println(student.getAddress());


        System.out.println(builder.toString());




    }
}
