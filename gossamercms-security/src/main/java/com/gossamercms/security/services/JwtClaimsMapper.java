package com.gossamercms.security.services;

import com.gossamercms.security.jwt.JwtUser;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class JwtClaimsMapper {

    public JwtUser toUser(Claims claims) {

        UUID userId = UUID.fromString(claims.getSubject());
        String identifier = extractString(claims.get("identifier"));
        String sessionId = extractString(claims.get("sessionId"));
        String[] roles = extractArray(claims.get("roles"));
        String[] permissions = extractArray(claims.get("permissions"));
        String userContextId = extractString(claims.get("userContextId"));
        UUID adminUserId = extractUuid(claims.get("adminUserId"));
        UUID masqueradingUserId = extractUuid(claims.get("masqueradingUserId"));
        String adminUserContextId = extractString(claims.get("adminUserContextId"));
        String adminIdentifier = extractString(claims.get("adminIdentifier"));
        String[] adminRoles = extractArray(claims.get("adminRoles"));
        String[] adminPermissions = extractArray(claims.get("adminPermissions"));

        return new JwtUser(
                userId,
                identifier,
                roles,
                permissions,
                sessionId,
                userContextId,
                adminUserId,
                masqueradingUserId,
                adminUserContextId,
                adminIdentifier,
                adminRoles,
                adminPermissions
        );
    }

    private String[] extractArray(Object raw) {
        if (raw == null) return new String[0];

        if (raw instanceof List<?> list) {
            return list.stream()
                    .map(String::valueOf)
                    .toArray(String[]::new);
        }

        if (raw instanceof String s) {
            return new String[]{s};
        }

        return new String[0];
    }

    private String extractString(Object raw) {
        if (raw == null) {
            return null;
        }

        return String.valueOf(raw);
    }

    private UUID extractUuid(Object raw) {
        if (raw == null) {
            return null;
        }

        if (raw instanceof UUID uuid) {
            return uuid;
        }

        return UUID.fromString(String.valueOf(raw));
    }


    public JwtUser fromToken(
            String token,
            JwtService jwtService
    ) {
        return toUser(
                jwtService
                        .parseToken(token)
                        .getPayload()
        );
    }
}
