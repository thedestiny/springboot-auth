package com.platform.contract.esign;

import com.platform.contract.exception.ApiCodeEnum;
import com.platform.contract.exception.ApiException;
import lombok.Data;

import java.io.File;

@Data
public class EsignFileBean {

    private String fileName;
    private int fileSize;
    private String fileContentMD5;
    private String filePath;

    public EsignFileBean(String filePath) {

        this.filePath = filePath;
        this.fileContentMD5 = FileTransformation.getFileContentMD5(filePath);
        File file = new File(filePath);
        if (!file.exists()) {
            throw new ApiException(ApiCodeEnum.ERROR_BIZ, "文件不存在", filePath);
        } else {
            this.fileName = file.getName();
            this.fileSize = (int)file.length();
        }
    }

    public byte[] getFileBytes() {
        return FileTransformation.fileToBytes(this.filePath);
    }

}
