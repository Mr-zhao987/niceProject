package com.ruoyi.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密解密工具类
 *
 * @author ruoyi
 */
public class EncryptUtils
{
    /** AES算法 */
    private static final String AES = "AES";

    /** AES算法模式（CBC + PKCS5Padding） */
    private static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";

    /** AES密钥长度 */
    private static final int AES_KEY_SIZE = 128;

    /** 默认AES密钥（生产环境请替换，建议从配置中心读取） */
    private static final String DEFAULT_AES_KEY = "RuoYiVue3.8.7Key";

    /** 默认AES向量 */
    private static final String DEFAULT_AES_IV = "RuoYiVueIv123456";

    /** 字符集 */
    private static final String UTF_8 = "UTF-8";

    /**
     * MD5加密
     *
     * @param data 待加密数据
     * @return MD5（32位小写）
     */
    public static String md5(String data)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        }
        catch (Exception e)
        {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * SHA-256加密
     *
     * @param data 待加密数据
     * @return SHA-256（64位小写）
     */
    public static String sha256(String data)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        }
        catch (Exception e)
        {
            throw new RuntimeException("SHA-256加密失败", e);
        }
    }

    /**
     * Base64编码
     */
    public static String base64Encode(String data)
    {
        return Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     */
    public static String base64Decode(String data)
    {
        return new String(Base64.getDecoder().decode(data), StandardCharsets.UTF_8);
    }

    /**
     * Base64编码（字节数组）
     */
    public static String base64Encode(byte[] data)
    {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Base64解码为字节数组
     */
    public static byte[] base64DecodeToBytes(String data)
    {
        return Base64.getDecoder().decode(data);
    }

    /**
     * 生成AES密钥（Base64编码）
     */
    public static String generateAesKey()
    {
        try
        {
            KeyGenerator kg = KeyGenerator.getInstance(AES);
            kg.init(AES_KEY_SIZE, new SecureRandom());
            SecretKey secretKey = kg.generateKey();
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }
        catch (Exception e)
        {
            throw new RuntimeException("AES密钥生成失败", e);
        }
    }

    /**
     * AES加密（CBC模式，默认密钥和向量）
     *
     * @param plainText 明文
     * @return Base64编码的密文
     */
    public static String aesEncrypt(String plainText)
    {
        return aesEncrypt(plainText, DEFAULT_AES_KEY, DEFAULT_AES_IV);
    }

    /**
     * AES加密（CBC模式）
     *
     * @param plainText 明文
     * @param key       密钥
     * @param iv        向量（16位）
     * @return Base64编码的密文
     */
    public static String aesEncrypt(String plainText, String key, String iv)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF_8), AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e)
        {
            throw new RuntimeException("AES加密失败", e);
        }
    }

    /**
     * AES解密（CBC模式，默认密钥和向量）
     *
     * @param cipherText Base64编码的密文
     * @return 明文
     */
    public static String aesDecrypt(String cipherText)
    {
        return aesDecrypt(cipherText, DEFAULT_AES_KEY, DEFAULT_AES_IV);
    }

    /**
     * AES解密（CBC模式）
     *
     * @param cipherText Base64编码的密文
     * @param key        密钥
     * @param iv         向量（16位）
     * @return 明文
     */
    public static String aesDecrypt(String cipherText, String key, String iv)
    {
        try
        {
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
            SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(UTF_8), AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cipherText));
            return new String(decrypted, UTF_8);
        }
        catch (Exception e)
        {
            throw new RuntimeException("AES解密失败", e);
        }
    }

    /**
     * 字节数组转十六进制字符串
     */
    public static String bytesToHex(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes)
        {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    /**
     * 十六进制字符串转字节数组
     */
    public static byte[] hexToBytes(String hex)
    {
        int len = hex.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2)
        {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return bytes;
    }
}
