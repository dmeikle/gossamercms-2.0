package com.gossamercms.rbac.models;

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
public class UserRole implements BaseModel {

    private UUID id;
    private UUID userId;
    private UUID roleId;
    private UUID assignedBy;
    private Instant assignedAt;
    private Instant expiresAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("user_roles")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("userId", UUID.class)
            .column("roleId", UUID.class)
            .column("assignedBy", UUID.class)
            .column("assignedAt", Instant.class)
            .column("expiresAt", Instant.class)
            .defaultSort("assignedAt desc")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }
}