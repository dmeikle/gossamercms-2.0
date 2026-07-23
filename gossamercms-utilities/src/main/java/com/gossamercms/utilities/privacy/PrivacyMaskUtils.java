package com.gossamercms.utilities.privacy;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PrivacyMaskUtils {

    public static String maskEmail(String email) {

        if (email == null || !email.contains("@")) {
            return null;
        }

        String[] parts = email.split("@", 2);

        String local = parts[0];
        String domain = parts[1];

        if (local.length() <= 1) {
            return "*@" + domain;
        }

        if (local.length() == 2) {
            return local.charAt(0) + "*@" + domain;
        }

        return local.charAt(0)
                + "***"
                + local.charAt(local.length() - 1)
                + "@"
                + domain;
    }

    public static String maskPhone(String phone) {

        if (phone == null || phone.isBlank()) {
            return null;
        }

        String digits = phone.replaceAll("\\D", "");

        if (digits.length() <= 4) {
            return "****";
        }

        String lastFour = digits.substring(digits.length() - 4);

        return "***-***-" + lastFour;
    }

    public static String maskName(String name) {

        if (name == null || name.isBlank()) {
            return null;
        }

        String[] parts = name.trim().split("\\s+");

        return Arrays.stream(parts)
                .map(part -> {
                    if (part.length() == 1) {
                        return "*";
                    }

                    return part.charAt(0)
                            + "***";
                })
                .collect(Collectors.joining(" "));
    }
}
