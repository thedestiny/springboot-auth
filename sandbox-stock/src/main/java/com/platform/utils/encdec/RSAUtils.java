package com.platform.utils.encdec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description RSA 加密工具类
 * @Author liangkaiyang
 * @Date 2025-06-04 10:53 AM
 */
public class RSAUtils {

    private RSAUtils() {
    }

    public static final String RSA_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";


    /**
     * 生成公私钥对
     *
     * @param keySize
     * @return
     */
    public static Map<String, String> createKeys(int keySize) {
        //为RSA算法创建一个KeyPairGenerator对象
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("No such algorithm-->[" + RSA_ALGORITHM + "]");
        }

        //初始化KeyPairGenerator对象,密钥长度
        kpg.initialize(keySize);                //keySize 可以为1024
        //生成密匙对
        KeyPair keyPair = kpg.generateKeyPair();
        //得到公钥
        Key publicKey = keyPair.getPublic();
        String publicKeyStr = Base64.encodeBase64URLSafeString(publicKey.getEncoded());
        //得到私钥
        Key privateKey = keyPair.getPrivate();
        String privateKeyStr = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        Map<String, String> keyPairMap = new HashMap<>();
        keyPairMap.put("publicKey", publicKeyStr);
        keyPairMap.put("privateKey", privateKeyStr);

        return keyPairMap;
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static String publicEncrypt(String data, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        RSAPublicKey publicKeyObj = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKeyObj);
        return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8), publicKeyObj.getModulus().bitLength()));
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static String privateDecrypt(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        RSAPrivateKey privateKeyObj = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKeyObj);
        return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), privateKeyObj.getModulus().bitLength()), StandardCharsets.UTF_8);
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static String privateEncrypt(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        RSAPrivateKey privateKeyObj = getPrivateKey(privateKey);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, privateKeyObj);
        return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(StandardCharsets.UTF_8), privateKeyObj.getModulus().bitLength()));
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws InvalidKeyException
     * @throws Exception
     */
    public static String publicDecrypt(String data, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException {
        RSAPublicKey publicKeyObj = getPublicKey(publicKey);
        Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, publicKeyObj);
        return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKeyObj.getModulus().bitLength()), StandardCharsets.UTF_8);
    }

    /**
     * 私钥生成数字签名
     *
     * @param data       待签名字符串
     * @param privateKey 私钥
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws Exception
     */
    public static String sign(String data, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {
        PrivateKey privateK = getPrivateKey(privateKey);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return Base64.encodeBase64URLSafeString(signature.sign());
    }

    /**
     * 公钥验证签名
     *
     * @param data
     * @param publicKey
     * @param sign
     * @return
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws SignatureException
     * @throws Exception
     */
    public static boolean verify(String data, String publicKey, String sign) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, SignatureException {

        PublicKey publicK = getPublicKey(publicKey);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return signature.verify(Base64.decodeBase64(sign));
    }

    /**
     * 得到公钥
     * 公钥字符串转换为RSAPublicKey对象
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    private static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        return (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
    }

    /**
     * 得到私钥
     * 私钥字符串转换为RSAPrivateKey对象
     *
     * @param privateKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    private static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
    }

    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常");
        }
        byte[] resultDatas = out.toByteArray();
        IOUtils.closeQuietly(out);
        return resultDatas;
    }

    // private static final BASE64Decoder decoder = new BASE64Decoder();

    // private static final BASE64Encoder encoder = new BASE64Encoder();

    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64.decodeBase64(key);
        // return decoder.decodeBuffer(key);
    }

    public static String encryptBASE64(byte[] key) {
        return Base64.encodeBase64String(key);
        // return encoder.encodeBuffer(key);
    }

    public static String decryptByPrivateKey(String s, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, privateKey);
        return new String(cipher.doFinal(decryptBASE64(s)));
    }

    public static String decryptByPublicKey(String s, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(2, publicKey);
        return new String(cipher.doFinal(decryptBASE64(s)));
    }

    public static String encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, publicKey);
        return encryptBASE64(cipher.doFinal(data));
    }

    public static String encryptByPrivateKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = decryptBASE64(key);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, privateKey);
        return encryptBASE64(cipher.doFinal(data));
    }

    public static void main(String[] args) {

        Map<String, String> keys = RSAUtils.createKeys(1024);
        keys.forEach((key, value) -> {
            System.out.println("");
            System.out.println(value);
        });
        System.out.println("");



    }

}
