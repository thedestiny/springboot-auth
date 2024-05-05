package com.platform.controller;

import cn.hutool.core.util.IdUtil;

import com.platform.utils.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Slf4j
@RestController
@RequestMapping(value = "api/zip")
public class ZipController {


    @GetMapping(value = "file")
    public void zipFile(HttpServletResponse response) throws Exception {


        MultipartFile file = getMultipartFile(new File(""), "123456");
        ResponseHelper.response(response, file.getInputStream());


    }



    //生成密码压缩文件
    private static File getZipFile(File file, String password) throws ZipException {

        String uuid = IdUtil.fastSimpleUUID();
        ZipFile zipFile = new ZipFile(uuid + ".zip", password.toCharArray());
        ZipParameters params = new ZipParameters();
        // 压缩级别
        params.setCompressionMethod(CompressionMethod.DEFLATE);
        params.setCompressionLevel(CompressionLevel.NORMAL);
        params.setEncryptFiles(true);
        params.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
        params.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
        zipFile.addFile(file, params);
        return zipFile.getFile();
    }

    // 转换File为MultipartFile
    public static MultipartFile getMultipartFile(File file, String pwd) throws Exception {


        File zipFile = getZipFile(file, pwd);

        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        MultipartFile multipartFile = new CommonsMultipartFile(item);
        if (file != null && file.exists()) {
            file.delete();
        }
        if (zipFile != null && zipFile.exists()) {
            zipFile.delete();
        }
        return multipartFile;
    }


    public static void writeFile(HttpServletResponse resp, InputStream inputStream) {
        OutputStream out = null;
        try {
            out = resp.getOutputStream();
            int len = 0;
            byte[] b = new byte[1024];
            while ((len = inputStream.read(b)) != -1) {
                out.write(b, 0, len);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }









    public static void main(String[] args) {


    }

}
