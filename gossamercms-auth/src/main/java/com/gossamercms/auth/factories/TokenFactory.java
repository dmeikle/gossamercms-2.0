package com.gossamercms.auth.factories;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenFactory {

    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateRefreshToken() {
        byte[] bytes = new byte[32]; // 256 bits
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
