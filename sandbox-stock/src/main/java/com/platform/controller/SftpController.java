package com.platform.controller;


import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelSftp;
import com.platform.utils.FtpConfig;
import com.platform.utils.SftpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * sftp 文件上传下载控制器
 */
@Slf4j
@Controller
@RequestMapping(value = "api/sftp")
public class SftpController {

    @Autowired
    private SftpUtils sftp;
    @Autowired
    private FtpConfig config;

    /**
     * 上传文件
     */
    @PostMapping(value = "upload")
    @ResponseBody
    public String upload(MultipartFile file) {

        try {
            sftp.initSftp(config);
            // 本地上传到 sftp
            sftp.upload(config.getUploadPath(), "./name.txt");
            // 上传文件然后上传到 sftp
            sftp.upload(config.getUploadPath(), "name.txt", file.getInputStream());
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    /**
     * 下载文件
     */
    @PostMapping(value = "download")
    @ResponseBody
    public String download(HttpServletResponse response) {

        try {
            sftp.initSftp(config);
            // 从 sftp 下载文件到本地
            sftp.download(config.getUploadPath(), "name.txt", "./savefile.txt");
            // 从 sftp 下载文件，然后写入 response
            sftp.download(config.getUploadPath(), "name.txt", response);
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    /**
     * 文档中的文件
     */
    @PostMapping(value = "list")
    @ResponseBody
    public String listFile(HttpServletResponse response) {

        try {
            sftp.initSftp(config);
            List<ChannelSftp.LsEntry> files = sftp.listFiles(config.getUploadPath());
            for (ChannelSftp.LsEntry file : files) {
                log.info("file name {}", JSONObject.toJSONString(file));
            }
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    /**
     * 删除文件
     */
    @PostMapping(value = "del")
    @ResponseBody
    public String delete(HttpServletResponse response) {

        try {
            sftp.initSftp(config);
            // 从 sftp 上删除文件
            sftp.delete(config.getUploadPath(), "name.txt");
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

}
