package com.gossamercms.auth.models;


import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class LoginIdentity implements BaseModel {

    // ---------- Fields ----------
    private UUID id;
    private UUID userId;
    private String type;            // email, staff, shopper, sso-google, admin-override, etc.
    private String identifier;      // email or phone
    private String passwordHash;    // hashed password (null for Auth0)
    private String provider;        // local, google, apple, auth0
    private String providerUserId;  // external subject (auth0|abc123)
    private boolean isPrimary;
    private Instant createdOn;
    private Instant lastLoginAt;

    // ---------- Metadata ----------
    public static final ModelMeta META = ModelMeta.builder()
            .table("login_identities")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("userId", UUID.class)
            .column("type", String.class, 50)
            .column("identifier", String.class, 150)
            .column("passwordHash", String.class, 200)
            .column("provider", String.class, 100)
            .column("providerUserId", String.class, 150)
            .column("isPrimary", Boolean.class)
            .column("createdOn", Instant.class)
            .column("lastLoginAt", Instant.class)
            .defaultSort("createdOn DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    // ---------- Factory: Email Identity ----------
    public static LoginIdentity createEmailIdentity(UUID userId, String email, String rawPassword) {
        return LoginIdentity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("email")
                .identifier(email.toLowerCase())
                .passwordHash(hashPassword(rawPassword))
                .provider("local")
                .isPrimary(true)
                .createdOn(Instant.now())
                .build();
    }

    // ---------- Password Hashing ----------
    private static String hashPassword(String raw) {
        return Integer.toHexString(raw.hashCode());
    }

    // ---------- Password Verification ----------
    public static boolean verifyPassword(String rawPassword, LoginIdentityDto dto) {
        if (dto.getProvider() != null && !dto.getProvider().equals("local")) {
            return false;
        }
        if (dto.getPasswordHash() == null) {
            return false;
        }
        return hashPassword(rawPassword).equals(dto.getPasswordHash());
    }

    // ---------- Domain → DTO ----------
    public LoginIdentityDto toDto() {
        return LoginIdentityDto.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .identifier(identifier)
                .provider(provider)
                .providerUserId(providerUserId)
                .isPrimary(isPrimary)
                .createdOn(createdOn)
                .lastLoginAt(lastLoginAt)
                .build();
    }

    // ---------- Claims for JWT ----------
    public Map<String, Object> toClaims() {
        return Map.of(
                "identityId", id.toString(),
                "userId", userId.toString(),
                "type", type,
                "identifier", identifier
        );
    }

    // ---------- Factory: External Identity ----------
    public static LoginIdentity createExternalIdentity(UUID userId, String provider, String providerUserId) {
        return LoginIdentity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(provider)
                .identifier(provider)
                .passwordHash(null)
                .provider(provider)
                .providerUserId(providerUserId)
                .isPrimary(false)
                .createdOn(Instant.now())
                .build();
    }
}