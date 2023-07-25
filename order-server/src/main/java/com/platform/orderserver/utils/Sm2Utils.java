package com.platform.orderserver.utils;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;

import java.security.PrivateKey;
import java.security.PublicKey;

@Slf4j
public class Sm2Utils {


    /**
     * SM2加密算法
     *
     * @param publicKey 公钥
     * @param text      数据
     * @return
     */
    public static String encrypt(String publicKey, String text) {
        ECPublicKeyParameters ecPublicKeyParameters = null;
        //这里需要根据公钥的长度进行加工
        if (publicKey.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKey = publicKey.substring(2);
            String xhex = publicKey.substring(0, 64);
            String yhex = publicKey.substring(64, 128);
            ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        } else {
            PublicKey p = BCUtil.decodeECPoint(publicKey, SmUtil.SM2_CURVE_NAME);
            ecPublicKeyParameters = BCUtil.toParams(p);
        }
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        // 公钥加密
        return sm2.encryptBcd(text, KeyType.PublicKey);
    }

    /**
     * SM2加密算法
     *
     * @param publicKey 公钥
     * @param text      明文数据
     * @return
     */
    public static String encrypt(PublicKey publicKey, String text) {
        ECPublicKeyParameters ecPublicKeyParameters = BCUtil.toParams(publicKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);
        // 公钥加密
        return sm2.encryptBcd(text, KeyType.PublicKey);
    }

    /**
     * SM2解密算法
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return
     */
    public static String decrypt(String privateKey, String cipherData) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toSm2Params(privateKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);
        // 私钥解密
        return StrUtil.utf8Str(sm2.decryptFromBcd(cipherData, KeyType.PrivateKey));
    }

    /**
     * SM2解密算法
     *
     * @param privateKey 私钥
     * @param cipherData 密文数据
     * @return
     */
    public static String decrypt(PrivateKey privateKey, String cipherData) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toParams(privateKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);
        // 私钥解密
        return StrUtil.utf8Str(sm2.decryptFromBcd(cipherData, KeyType.PrivateKey));
    }

    /**
     * 私钥签名
     *
     * @param privateKey 私钥
     * @param content    待签名内容
     * @return
     */
    public static String sign(String privateKey, String content) {
        ECPrivateKeyParameters ecPrivateKeyParameters = BCUtil.toSm2Params(privateKey);
        //创建sm2 对象
        SM2 sm2 = new SM2(ecPrivateKeyParameters, null);
        String sign = sm2.signHex(HexUtil.encodeHexStr(content));
        return sign;
    }

    /**
     * 验证签名
     *
     * @param publicKey 公钥
     * @param content   待签名内容
     * @param sign      签名值
     * @return
     */
    public static boolean verify(String publicKey, String content, String sign) {
        ECPublicKeyParameters ecPublicKeyParameters = null;
        //这里需要根据公钥的长度进行加工
        if (publicKey.length() == 130) {
            //这里需要去掉开始第一个字节 第一个字节表示标记
            publicKey = publicKey.substring(2);
            String xhex = publicKey.substring(0, 64);
            String yhex = publicKey.substring(64, 128);
            ecPublicKeyParameters = BCUtil.toSm2Params(xhex, yhex);
        } else {
            PublicKey p = BCUtil.decodeECPoint(publicKey, SmUtil.SM2_CURVE_NAME);
            ecPublicKeyParameters = BCUtil.toParams(p);
        }
        //创建sm2 对象
        SM2 sm2 = new SM2(null, ecPublicKeyParameters);

        boolean verify = sm2.verifyHex(HexUtil.encodeHexStr(content), sign);
        return verify;
    }


    public static void main(String[] args) {

        SM2 sm2 = SmUtil.sm2();
        //这里会自动生成对应的随机秘钥对
        byte[] privateKey = BCUtil.encodeECPrivateKey(sm2.getPrivateKey());
        //这里默认公钥压缩  公钥的第一个字节用于表示是否压缩 02或者03表示是压缩公钥,04表示未压缩公钥
        byte[] publicKey = BCUtil.encodeECPublicKey(sm2.getPublicKey());
        //这里得到未压缩的公钥  byte[] publicKey = ((BCECPublicKey) sm2.getPublicKey()).getQ().getEncoded(false);
        String priKey = HexUtil.encodeHexStr(privateKey);
        String pubKey = HexUtil.encodeHexStr(publicKey);
        System.out.println("私钥" + priKey);
        System.out.println("公钥" + pubKey);
        //明文
        String text = "123123123";
        System.out.println("测试明文文本" + text);
        //签名测试
        String sign = sign(priKey, text);
        System.out.println("生成签名" + sign);
        //验签测试
        boolean verify = verify(pubKey, text, sign);
        System.out.println("验签结果" + verify);

        //加解密测试
        String encryptData = encrypt(pubKey, text);
        System.out.println("加密结果" + encryptData);
        String decryptData = decrypt(priKey, encryptData);
        System.out.println("解密结果" + decryptData);

    }


}
