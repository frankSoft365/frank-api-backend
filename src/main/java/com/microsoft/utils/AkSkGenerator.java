package com.microsoft.utils;

import org.mindrot.jbcrypt.BCrypt;

import java.security.SecureRandom;
import java.util.Random;

public class AkSkGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final Random RANDOM = new Random();
    private static final String CHAR_POOL = "23456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";

    // 生成唯一AK
    public static String generateAK(String userId) {
        String userIdPart = userId.substring(0, Math.min(userId.length(), 6));
        userIdPart = String.format("%-6s", userIdPart).replace(' ', '0');
        String timestampPart = String.valueOf(System.currentTimeMillis()).substring(8);
        String randomPart = generateRandomString(6);
        return userIdPart + timestampPart + randomPart;
    }

    // 生成32位SK
    public static String generateSK() {
        return generateRandomString(32);
    }

    // SK哈希（仅存哈希值）
    public static String[] hashSecretKey(String secretKey) {
        String salt = BCrypt.gensalt(10);
        String skHash = BCrypt.hashpw(secretKey, salt);
        return new String[]{skHash, salt};
    }

    private static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL.charAt(RANDOM.nextInt(CHAR_POOL.length())));
        }
        return sb.toString();
    }
}