package com.minan.game.utils;

import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestUtil;

import java.security.SecureRandom;

public class IdGenerator {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String generateId() {
        return UUID.randomUUID().toString(true);
    }

    public static String generateId(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString(true);
    }

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(SECURE_RANDOM.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String generateUserId() {
        return generateRandomString(64);
    }

    public static String generateLevelId() {
        return generateRandomString(64);
    }

    public static String generateSceneId() {
        return generateRandomString(64);
    }

    public static String generatePromptId() {
        return generateRandomString(64);
    }

    public static String generateAchievementId() {
        return generateRandomString(64);
    }

    public static String generateNpcId() {
        return generateRandomString(64);
    }

    public static String generateResourceId() {
        return generateRandomString(64);
    }

    public static String generateHintId() {
        return generateRandomString(64);
    }

    public static String generateTaskId() {
        return generateRandomString(64);
    }

    public static String generateDimensionId() {
        return generateRandomString(64);
    }

    public static String generateCategoryId() {
        return generateRandomString(64);
    }

    public static String generatePointId() {
        return generateRandomString(64);
    }

    public static String generateProgressId() {
        return generateRandomString(64);
    }

    public static String generateQuizId() {
        return generateRandomString(64);
    }

    public static String generateRecordId() {
        return generateRandomString(64);
    }

    public static String encryptPassword(String password) {
        return DigestUtil.md5Hex(password);
    }
}
