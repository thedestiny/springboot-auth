package com.platform.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码
 *
 * @author 小柒2012
 */
@Slf4j
public class ZxingUtils {

    static final String FORMAT = "png";
    static final int height = 256;

    /**
     * 生成二维码
     */
    public static void createQRCodeImage(String qrCode, String imgPath) {

        Map<EncodeHintType, Object> hashMap = new HashMap<>();
        hashMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hashMap.put(EncodeHintType.MARGIN, 2);
        try {
            // 生成支付单二维码
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(qrCode, BarcodeFormat.QR_CODE, height, height, hashMap);
            MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, new File(imgPath).toPath());
        } catch (WriterException e) {
            log.info("encounter error is {}", e.getMessage(), e);
        } catch (IOException e) {
            log.info("error is {}", e.getMessage(), e);
        }
    }


    public static void main(String[] args) {

        String qrCode = "weixin://wxpay/bizpayurl?pr=bU33nCnzz";
        String property = System.getProperty("user.dir");
        createQRCodeImage(qrCode, property + "/123.jpg");

    }


}
