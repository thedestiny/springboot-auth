package com.platform.contract.config;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import org.apache.commons.codec.binary.Base64;

public class FileTransformation {

    private FileTransformation() {
    }

    public static byte[] fileToBytes(String srcFilePath) {
        return getBytes(srcFilePath);
    }

    public static String getFileContentMD5(String filePath) {
        byte[] bytes = getFileMD5Bytes128(filePath);
        return Base64.encodeBase64String(bytes);
    }

    public static byte[] getFileMD5Bytes128(String filePath) {
        FileInputStream fis = null;

        byte[] var6;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];

            int length;
            while((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
            }

            var6 = md5.digest();
        } catch (FileNotFoundException var17) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "文件找不到", filePath);
        } catch (NoSuchAlgorithmException var18) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "不支持此算法", filePath);
        } catch (IOException var19) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "输入流或输出流异常", filePath);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException var16) {
                    throw new ApiException(ApiCodeEnum.ERROR_BIZ, "关闭文件输入流失败", filePath);
                }
            }

        }

        return var6;
    }

    public static byte[] getBytes(String filePath) {
        File file = new File(filePath);
        FileInputStream fis = null;

        byte[] var6;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];

            int numRead;
            for(int offset = 0; offset < buffer.length && (numRead = fis.read(buffer, offset, buffer.length - offset)) >= 0; offset += numRead) {
            }

            var6 = buffer;
        } catch (Exception var15) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "获取文件字节流失败", filePath);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException var14) {
                    throw new ApiException(ApiCodeEnum.ERROR_BIZ, "关闭文件输入流失败", filePath);
                }
            }

        }

        return var6;
    }

    public static boolean downLoadFileByUrl(String httpUrl, String dir) {
        InputStream fis = null;
        FileOutputStream fileOutputStream = null;

        try {
            URL url = new URL(httpUrl);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.connect();
            fis = httpConn.getInputStream();
            fileOutputStream = new FileOutputStream(new File(dir));
            byte[] buffer = new byte[1024];

            int length;
            while((length = fis.read(buffer, 0, 1024)) != -1) {
                fileOutputStream.write(buffer, 0, length);
            }
        } catch (IOException var15) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "获取文件流异常", new Object[0]);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException var14) {
                throw new ApiException(ApiCodeEnum.ERROR_BIZ, "关闭文件流异常", new Object[0]);
            }

        }

        return true;
    }

    public static Map<String, Object> fileUrlToBytes(String fileUrl) {

        Map<String, Object> map = new HashMap<>();
        InputStream fis = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.connect();
            fis = httpConn.getInputStream();
            map.put("fileSize", fis.available());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];

            int length;
            while((length = fis.read(buffer, 0, 1024)) != -1) {
                md5.update(buffer, 0, length);
                outStream.write(buffer, 0, length);
            }

            byte[] md5Bytes = md5.digest();
            byte[] fileData = outStream.toByteArray();
            map.put("fileData", fileData);
            map.put("md5Bytes", md5Bytes);
        } catch (IOException var19) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "获取文件流异常", fileUrl);
        } catch (NoSuchAlgorithmException var20) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "文件计算异常", fileUrl);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }

                outStream.close();
            } catch (IOException var18) {
                throw new ApiException(ApiCodeEnum.ERROR_BIZ, "关闭文件流异常", fileUrl);
            }
        }

        return map;
    }
}
