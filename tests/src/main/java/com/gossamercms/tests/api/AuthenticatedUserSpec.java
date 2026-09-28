package com.gossamercms.tests.api;

import com.gossamercms.security.jwt.JwtUser;

import java.util.List;
import java.util.UUID;

public record AuthenticatedUserSpec(
        UUID userId,
        String identifier,
        List<String> roles,
        List<String> permissions,
        String sessionId,
        String userContextId
) {

    public JwtUser toJwtUser() {
        return new JwtUser(
                userId,
                identifier,
                roles == null ? new String[0] : roles.toArray(String[]::new),
                permissions == null ? new String[0] : permissions.toArray(String[]::new),
                sessionId,
                userContextId
        );
    }
}
