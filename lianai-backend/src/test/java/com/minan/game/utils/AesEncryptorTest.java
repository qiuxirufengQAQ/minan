package cn.qrfeng.lianai.game.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AES 加密工具测试
 */
@SpringBootTest
@ActiveProfiles("test")
class AesEncryptorTest {

    @Autowired
    private AesEncryptor aesEncryptor;

    @BeforeEach
    void setUp() {
        assertNotNull(aesEncryptor, "AesEncryptor 应该被注入");
    }

    @Test
    void testEncryptAndDecrypt() {
        // 给定
        String plainText = "这是一段测试对话内容";

        // 当
        String encrypted = aesEncryptor.encrypt(plainText);
        String decrypted = aesEncryptor.decrypt(encrypted);

        // 则
        assertNotNull(encrypted, "加密结果不应为空");
        assertNotEquals(plainText, encrypted, "加密后的内容应该与原文不同");
        assertEquals(plainText, decrypted, "解密后应该与原文相同");
    }

    @Test
    void testEncryptEmptyString() {
        // 给定
        String plainText = "";

        // 当
        String encrypted = aesEncryptor.encrypt(plainText);
        String decrypted = aesEncryptor.decrypt(encrypted);

        // 则
        assertEquals("", encrypted, "空字符串加密后仍为空");
        assertEquals("", decrypted, "空字符串解密后仍为空");
    }

    @Test
    void testEncryptNull() {
        // 当
        String encrypted = aesEncryptor.encrypt(null);
        String decrypted = aesEncryptor.decrypt(null);

        // 则
        assertNull(encrypted, "null 加密后仍为 null");
        assertNull(decrypted, "null 解密后仍为 null");
    }

    @Test
    void testEncryptLongText() {
        // 给定
        String plainText = "这是一段很长的测试文本，包含多轮对话内容。\n" +
                          "用户：你好，我想学习如何更好地沟通。\n" +
                          "NPC：很高兴你主动学习！沟通的关键在于倾听和表达。\n" +
                          "用户：那具体应该怎么做呢？\n" +
                          "NPC：首先，要认真听对方说话，不要急于打断...";

        // 当
        String encrypted = aesEncryptor.encrypt(plainText);
        String decrypted = aesEncryptor.decrypt(encrypted);

        // 则
        assertNotNull(encrypted, "长文本加密结果不应为空");
        assertEquals(plainText, decrypted, "长文本解密后应该与原文相同");
    }

    @Test
    void testEncryptSpecialCharacters() {
        // 给定
        String plainText = "特殊字符测试：!@#$%^&*()_+-=[]{}|;':\",./<>?中文🚀";

        // 当
        String encrypted = aesEncryptor.encrypt(plainText);
        String decrypted = aesEncryptor.decrypt(encrypted);

        // 则
        assertEquals(plainText, decrypted, "特殊字符解密后应该与原文相同");
    }
}
