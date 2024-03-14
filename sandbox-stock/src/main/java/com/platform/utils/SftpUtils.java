package com.platform.utils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Properties;

/**
 * @Description ftp 上传文件
 * @Author kaiyang
 * @Date 2024-03-14 7:11 PM
 */

@Slf4j
public class SftpUtils {

    private Session session = null;
    private ChannelSftp channel = null;

    /**
     * 连接 sftp 服务器，创建 channel 和 session
     * @param username 用户名
     * @param host
     * @param port
     * @param privatKey
     * @param password
     * @throws JSchException
     */
    public void connectSftpServer(String username, String host, int port, String privatKey, String password) throws JSchException {
        JSch jsch = new JSch();
        // 根据用户名，主机ip，端口获取一个Session对象
        session = jsch.getSession(username, host, port);
//        if (StringUtils.isNotEmpty(privatKey)) {
//            jsch.addIdentity(privatKey);
//        }
        // 设置密码
        session.setPassword(password);
        // 为Session对象设置properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        // 通过Session建立链接
        session.connect();
        // 打开SFTP通道
        channel = (ChannelSftp) session.openChannel("sftp");
        // 建立SFTP通道的连接
        channel.connect();
    }



}
