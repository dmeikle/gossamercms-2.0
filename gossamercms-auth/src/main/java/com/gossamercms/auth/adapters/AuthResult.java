package com.gossamercms.auth.adapters;


import java.util.UUID;
import java.util.Map;

public record AuthResult(
        UUID userId,
        String email,
        Map<String, Object> claims
) {}