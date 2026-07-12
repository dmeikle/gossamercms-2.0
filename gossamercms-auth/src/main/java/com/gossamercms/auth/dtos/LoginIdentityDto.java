package com.gossamercms.auth.dtos;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class LoginIdentityDto implements DtoWithId {

    private UUID id;
    private UUID userId;
    private String type;            // email, staff, shopper, sso-google, etc.
    private String identifier;      // email or phone
    private String passwordHash;    // <-- REQUIRED for login
    private String provider;        // local, google, apple, admin-override
    private String providerUserId;  // SSO subject
    private boolean isPrimary;
    private Instant createdOn;
    private Instant lastLoginAt;

    @Override
    public UUID getId() {
        return id;
    }

    // Claims for JWT
    public Map<String, Object> toClaims() {
        return Map.of(
                "identityId", id.toString(),
                "userId", userId.toString(),
                "type", type,
                "identifier", identifier
        );
    }

    public Map<String, Object> toClaims(RoleDto role, String[] permissionNames) {
        return Map.of(
                "identityId", id.toString(),
                "userId", userId.toString(),
                "type", type,
                "identifier", identifier,
                "roles", List.of(role.getName()),
                "permissions", Arrays.asList(permissionNames)
        );
    }
}