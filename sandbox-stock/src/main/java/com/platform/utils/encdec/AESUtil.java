package com.platform.utils.encdec;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @Description AES 加密工具类
 * @Author liangkaiyang
 * @Date 2025-06-04 10:53 AM
 */
@Slf4j
public class AESUtil {

	private AESUtil() {
		throw new IllegalStateException("Utility class");
	}

	private static final String KEY_ALGORITHM = "AES";

	// jdk17 环境
	// private static final String DEFAULT_CIPHER_ALGORITHM = "AES/GCM/NoPadding";;// 默认的加密算法
	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/GCM/PKCS5Padding";;// 默认的加密算法

	/**
	 * AES 加密操作
	 *
	 * @param content     待加密内容
	 * @param password 加密密码
	 * @return 返回Base64转码后的加密数据
	 */
	public static String encrypt(String content, String password) {
		try {
			Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(password));
			byte[] iv = cipher.getIV();
			assert iv.length == 12;
			byte[] encryptData = cipher.doFinal(content.getBytes());
			assert encryptData.length == content.getBytes().length + 16;
			byte[] message = new byte[12 + content.getBytes().length + 16];
			System.arraycopy(iv, 0, message, 0, 12);
			System.arraycopy(encryptData, 0, message, 12, encryptData.length);
			return Base64.encodeBase64String(message);
		} catch (Exception e) {
			log.info("AESUtil#encrypt拋出异常,password:{},content:{}",password,content,e);
		}
		return null;
	}

	/**
	 * AES 解密操作
	 *
	 * @param base64Content
	 * @param password
	 * @return
	 */
	public static String decrypt(String base64Content, String password) {
		try {
			byte[] content = Base64.decodeBase64(base64Content);
			if (content.length < 12 + 16) {
				throw new IllegalArgumentException();
			}
			GCMParameterSpec params = new GCMParameterSpec(128, content, 0, 12);

				Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
				cipher.init(Cipher.DECRYPT_MODE, getSecretKey(password), params);
				byte[] decryptData = cipher.doFinal(content, 12, content.length - 12);
				return new String(decryptData);
		} catch (Exception e) {
			log.info("AESUtil#decrypt拋出异常,password:{},content:{}",password,base64Content,e);
		}
		return null;
	}

	/**
	 * 生成加密秘钥
	 *
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	private static SecretKeySpec getSecretKey(String encryptPass) throws NoSuchAlgorithmException {
		KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);

		SecureRandom random=SecureRandom.getInstance("SHA1PRNG");
        random.setSeed(encryptPass.getBytes());
		// 初始化密钥生成器，AES要求密钥长度为128位、192位、256位
		kg.init(128, random);
		SecretKey secretKey = kg.generateKey();
		return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);// 转换为AES专用密钥
	}

	public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
		cipher.init(2, new SecretKeySpec(decryptKey.getBytes(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		return new String(decryptBytes);
	}

	public static byte[] base64Decode(String base64Code) throws Exception {
		return StringUtils.isEmpty(base64Code) ? null :  cn.hutool.core.codec.Base64.decode(base64Code);//new BASE64Decoder()).decodeBuffer(base64Code);
	}

	public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
		return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
	}
}
