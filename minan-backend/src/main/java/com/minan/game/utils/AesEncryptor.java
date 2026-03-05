package com.minan.game.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 加密工具类
 * 用于加密敏感数据（如对话记录）
 */
@Slf4j
@Component
public class AesEncryptor {

    @Value("${app.aes.key:MinanDefaultKey2025!@#$}")
    private String aesKey;
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private SecretKeySpec secretKey;

    @PostConstruct
    public void init() {
        // 确保密钥长度为 16 字节（128 位）
        String key = aesKey;
        if (key.length() < 16) {
            key = String.format("%1$-16s", key).substring(0, 16);
        } else if (key.length() > 16) {
            key = key.substring(0, 16);
        }
        secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        log.info("AES 加密工具初始化完成，密钥长度：{} 字节", key.length());
    }

    /**
     * 加密字符串
     * @param plainText 明文
     * @return Base64 编码的密文
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("加密失败", e);
            throw new RuntimeException("加密失败：" + e.getMessage());
        }
    }

    /**
     * 解密字符串
     * @param cipherText Base64 编码的密文
     * @return 明文
     */
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密失败", e);
            throw new RuntimeException("解密失败：" + e.getMessage());
        }
    }
}
