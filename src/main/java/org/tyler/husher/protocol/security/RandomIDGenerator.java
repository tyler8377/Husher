package org.tyler.husher.protocol.security;

import java.security.SecureRandom;

public class RandomIDGenerator {

    private static final char[] BASE_62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

    public static String generate(char[] chars, int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++)
            sb.append(chars[random.nextInt(chars.length)]);
        return sb.toString();
    }

    public static String generateBase62() {
        return generate(BASE_62_CHARS, 12);
    }
}
