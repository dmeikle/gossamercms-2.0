package com.gossamercms.security.shared.factories;


import java.util.LinkedHashMap;
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
        return toClaims(userId, userContextId, sessionId, identifier, roleName, permissions, Map.of());
    }

    public static Map<String, Object> toClaims(
            UUID userId,
            UUID userContextId,
            String sessionId,
            String identifier,
            String roleName,
            String[] permissions,
            Map<String, Object> extraClaims
    ) {
        Map<String, Object> claims = new LinkedHashMap<>();
        claims.put("userId", userId.toString());
        claims.put("userContextId", userContextId.toString());
        claims.put("sessionId", sessionId);
        claims.put("identifier", identifier);
        claims.put("roles", List.of(roleName));
        claims.put("permissions", permissions);
        claims.putAll(extraClaims);
        return claims;
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
