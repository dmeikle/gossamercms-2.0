package com.gossamercms.auth.models;

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
public class RolePermission implements BaseModel {

    private UUID id;
    private UUID roleId;
    private UUID permissionId;
    private Instant createdAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("role_permissions")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("roleId", UUID.class)
            .column("permissionId", UUID.class)
            .column("createdAt", Instant.class)
            .defaultSort("createdAt desc")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }
}