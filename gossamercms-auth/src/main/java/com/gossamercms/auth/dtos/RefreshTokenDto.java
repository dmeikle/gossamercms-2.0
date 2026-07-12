package com.gossamercms.auth.dtos;

import com.gossamercms.auth.models.RefreshToken;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenDto implements DtoWithId {
    private UUID id;

    private String token;

    private String username;

    private Instant expiresAt;

    private boolean revoked;

    public RefreshToken toEntity() {
        return RefreshToken.builder()
                .id(id)
                .token(token)
                .username(username)
                .expiresAt(expiresAt)
                .revoked(revoked)
                .build();
    }
}
