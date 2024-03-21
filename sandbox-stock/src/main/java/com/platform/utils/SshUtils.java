package com.platform.utils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.ssh.JschUtil;
import com.google.common.collect.Lists;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;

/**
 * ssh 远程连接工具
 * @Description
 * @Author kaiyang
 * @Date 2024-03-18 9:12 AM
 */

@Slf4j
public class SshUtils {

    private static String address = "";
    private static Integer port = 22;
    private static String username = "root";
    private static String password = "";

    public static void main(String[] args) {


        BigDecimal amt = new BigDecimal(3.0d);
        BigDecimal bigDecimal = BigDecimal.valueOf(3.0d);

        List<String> stdout = Lists.newArrayList();

        String pwd = RandomUtil.randomString(10);
        System.out.println(pwd);
        Session session = null;
        Channel channel = null;

        // linux 执行命令
        String command = "pwd";

        try {
            session = JschUtil.openSession(address, port, username, password);
            session.setTimeout(60000);
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            BufferedReader input = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();
            log.info("执行命令: " + command);

            String line;
            while ((line = input.readLine()) != null) {
                stdout.add(line);
            }
            input.close();

            StringBuilder result = new StringBuilder();
            for (String str : stdout) {
                result.append("\n").append(str);
            }
            log.info("执行命令返回: " + result.toString());

        } catch (Exception e) {

            log.info("error is {} ", e.getMessage(), e);

        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }

        }


    }
}
