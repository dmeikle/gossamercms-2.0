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
        String identifier = claims.get("identifier", String.class);
        String sessionId = claims.get("sessionId", String.class);
        String[] roles = extractArray(claims.get("roles"));
        String[] permissions = extractArray(claims.get("permissions"));
        String userContextId = claims.get("userContextId", String.class);

        return new JwtUser(
                userId,
                identifier,
                roles,
                permissions,
                sessionId,
                userContextId
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
