package com.platform.controller;


import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.jcraft.jsch.ChannelSftp;
import com.platform.dto.SftpDto;
import com.platform.utils.FtpConfig;
import com.platform.utils.SftpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
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

    public static final String path;

    static {
        path = System.getProperty("user.dir");
        log.info("project path is {}", path);
    }

    /**
     * 上传文件
     * api/sftp/upload
     */
    @PostMapping(value = "upload/file")
    @ResponseBody
    public String uploadFile(MultipartFile file) {

        try {
            // file.getName() 参数名
            // file.getOriginalFilename() 文件的原始名称
            log.info("file {} name {}", file.getName(), file.getOriginalFilename());
            String date = DateUtil.format(new Date(), "yyyyMMdd");
            sftp.initSftp(config);
            // 上传文件然后上传到 sftp
            sftp.upload(config.getUploadPath() + File.separator + date, file.getOriginalFilename(), file.getInputStream());
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    @PostMapping(value = "upload")
    @ResponseBody
    public String upload(@RequestBody SftpDto dto) {

        try {
            String date = DateUtil.format(new Date(), "yyyyMMdd");
            String tmp = System.getProperty("user.dir");
            log.info("project path is {}", tmp);
            sftp.initSftp(config);
            // 本地上传到 sftp
            sftp.upload(config.getUploadPath() + File.separator + date, tmp + File.separator + dto.getFile());
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    /**
     * 下载文件
     */
    @PostMapping(value = "download/file")
    @ResponseBody
    public void downloadFile(@RequestBody SftpDto dto, HttpServletResponse response) {

        try {
            sftp.initSftp(config);
            // 从 sftp 下载文件，然后写入 response
            sftp.download(dto.getPath(), dto.getFile(), response);
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
    }

    /**
     * 下载文件
     */
    @PostMapping(value = "download")
    @ResponseBody
    public String download(@RequestBody SftpDto dto) {

        try {
            sftp.initSftp(config);
            String property = System.getProperty("user.dir");
            // 从 sftp 下载文件到本地
            String date = DateUtil.format(new DateTime(), "yyyyMMdd");
            // 文件目录
            String tagFile = property + File.separator + "data" + File.separator + dto.getFile();
            sftp.download(dto.getPath(), dto.getFile(), tagFile);
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    /**
     * 文档中的文件
     * /api/sftp/list
     */
    @PostMapping(value = "list")
    @ResponseBody
    public String listFile(@RequestBody SftpDto dto) {

        try {
            sftp.initSftp(config);
            List<ChannelSftp.LsEntry> files = sftp.listFiles(dto.getPath());
            for (ChannelSftp.LsEntry file : files) {
                // 过滤目录，只打印目录下的文件
                if(!file.getAttrs().isDir()){
                    log.info("file name {}", file.getFilename());
                }
            }
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

    /**
     * 删除文件或者目录
     */
    @PostMapping(value = "del")
    @ResponseBody
    public String delete(@RequestBody SftpDto dto) {

        try {
            sftp.initSftp(config);
            // sftp.information(dto.getPath());
            // 从 sftp 上删除文件
            sftp.delete(dto.getPath(), dto.getFile());
        } catch (Exception e) {
            log.error("error is {} ", e.getMessage(), e);
        }
        return "success";
    }

}
