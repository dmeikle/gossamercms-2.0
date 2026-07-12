package com.gossamercms.security.shared.factories;


import java.util.List;
import java.util.Map;
import java.util.UUID;

public  class JwtClaimsFactory {

    private JwtClaimsFactory() {
        // prevent instantiation
    }

    public static Map<String, Object> toClaims(
            UUID userId,
            UUID userContextId,
            String sessionId,
            String identifier,
            String roleName,
            String[] permissions
    ) {
        return Map.of(
                "userId", userId.toString(),
                "userContextId", userContextId,
                "sessionId", sessionId,
                "identifier", identifier,
                "roles", List.of(roleName),
                "permissions", permissions
        );
    }
//
//    public static Map<String, Object> toContextClaims(
//            UserDto user,
//            UserContextDto context,
//            AccountMappingDto account
//    ) {
//        Map<String, Object> claims = new HashMap<>();
//
//        claims.put("userId", user.getId().toString());
//        claims.put("contextId", context.getId().toString());
//        claims.put("contextType", context.getContextType());
//        claims.put("accountId", account.getAccountId().toString());
//        claims.put("roleId", account.getRoleId().toString());
//
//        return claims;
//    }
}

