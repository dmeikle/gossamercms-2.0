package com.gossamercms.auth.models;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.gossamercms.auth.dtos.RefreshTokenDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
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
public class RefreshToken implements BaseModel {


    private UUID id;

    private String token;

    private String username;

    private Instant expiresAt;

    private boolean revoked;


    public static final ModelMeta META = ModelMeta.builder()
            .table("refresh_tokens")
            .datasource("postgres")
            .column("id", UUID.class,  100)
            .column("token", String.class, 255)
            .column("username", String.class, 100)
            .column("expiresAt", Instant.class)
            .column("revoked", Boolean.class)
            .defaultSort("name asc")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public RefreshTokenDto toDto() {
        return RefreshTokenDto.builder()
                .id(id)
                .token(token)
                .username(username)
                .expiresAt(expiresAt)
                .revoked(revoked)
                .build();
    }
}
