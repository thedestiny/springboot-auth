package com.platform.controller;

import com.github.javafaker.Faker;
import com.platform.dto.Stu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * https://cloud.tencent.com/developer/article/2267078
 * protobuf
 *
 * @Description
 * @Author kaiyang
 * @Date 2024-04-08 6:31 下午
 */


@Slf4j
@RequestMapping(value = "api/proto")
@RestController
public class ProtoController {


    @GetMapping(value = "proto")
    public String proto() {

        return "";
    }

    public static void main(String[] args) throws InterruptedException {

        String host = "localhost"; // 目标主机IP地址
        int port = 10900; // 目标端口号
        String message = "Hello, TCP!"; // 要发送的消息

        Faker faker = new Faker(Locale.CHINESE);


        for (int i = 0; i < 100; i++) {

            Stu.Student.Builder builder = Stu.Student.newBuilder();
            builder.setAddress(faker.address().fullAddress());
            builder.setFlag(true);
            builder.setName(faker.name().fullName());
            builder.setWeight(34.5);
            builder.setDirect(Stu.Direction.DOWN);
            builder.setId(faker.number().numberBetween(2, 100));
            builder.setSeq(faker.number().randomNumber());
            // 构建对象
            Stu.Student build = builder.build();

            String s = build.toString();
            // 序列化
            byte[] bytes = build.toByteArray();
            sendMsg(host, port, s);

            TimeUnit.SECONDS.sleep(1);
        }


    }

    public static void sendMsg(String host, int port, String data) {

        try {
            Socket socket = new Socket(host, port);
            OutputStream stream = socket.getOutputStream();
            stream.write(data.getBytes(StandardCharsets.UTF_8));
            // BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
//            writer.write(data);
//            writer.newLine();
//            writer.flush();
            // byte[] data = message.getBytes();
            // outputStream.write(data);
            System.out.println("message send successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
