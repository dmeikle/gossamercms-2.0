package com.gossamercms.users.config;

import java.util.Map;

public final class UserContextDefaults {

    private static UserContextDefaultsProvider provider = new DefaultProvider();

    private UserContextDefaults() {}

    public static void setProvider(UserContextDefaultsProvider newProvider) {
        provider = newProvider;
    }

    public static Map<String, Object> forType(String contextType) {
        return provider.forType(contextType);
    }

    // Default implementation
    private static class DefaultProvider implements UserContextDefaultsProvider {
        @Override
        public Map<String, Object> forType(String contextType) {
            return switch (contextType) {
                case "default" -> Map.of(
                        "theme", "light",
                        "language", "en-US",
                        "timezone", "UTC",
                        "onboardingCompleted", false,
                        "defaultContext", true,
                        "homepage", "/members/dashboard"
                );
                case "admin" -> Map.of(
                        "theme", "dark",
                        "language", "en-US",
                        "timezone", "UTC",
                        "onboardingCompleted", false,
                        "defaultContext", true,
                        "homepage", "/admin/dashboard"
                );
                default -> throw new IllegalArgumentException("Unknown context type: " + contextType);
            };
        }
    }
}
