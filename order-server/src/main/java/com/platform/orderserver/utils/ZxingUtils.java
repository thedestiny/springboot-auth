package com.platform.orderserver.utils;

import com.alipay.api.internal.util.codec.Base64;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
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

    static final int height = 350;
    static final int weight = 350;

    /**
     * 生成二维码
     *
     * @param qrCode
     * @param imgPath
     */
    public static void createQRCodeImage(String qrCode, String imgPath) {

        Map<EncodeHintType, Object> hashMap = new HashMap<>();
        hashMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hashMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hashMap.put(EncodeHintType.MARGIN, 2);
        try {
            // 生成支付单二维码
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(qrCode, BarcodeFormat.QR_CODE, weight, height, hashMap);
            MatrixToImageWriter.writeToPath(bitMatrix, FORMAT, new File(imgPath).toPath());
        } catch (Exception e) {
            log.info("error is {} and detail ", e.getMessage(), e);
        }
    }

    /**
     * 生成图片流
     *
     * @param text 链接地址
     * @return
     * @throws WriterException
     * @throws IOException
     */
    public static String generateQRCodeImage(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, String> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, weight, height, hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            byte[] imgByte = Base64.encodeBase64(outputStream.toByteArray());
            return "data:image/png;base64," + new String(imgByte);
        } catch (Exception e) {
            log.info("error is {} and detail ", e.getMessage(), e);
            return "";
        }

    }


    public static void main(String[] args) {


        String qrCode = "weixin://wxpay/bizpayurl?pr=bU33nCnzz";

        createQRCodeImage(qrCode, "./123.jpg");

    }


}
