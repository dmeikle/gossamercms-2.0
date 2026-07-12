package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.AccountMappingDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class AccountMapping implements BaseModel {

    private UUID id;
    private UUID userContextId;
    private UUID accountId;
    private UUID roleId;
    private boolean isDefault;
    private Instant createdAt;
    private Instant expiresAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("account_mappings")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("userContextId", UUID.class)
            .column("accountId", UUID.class)
            .column("roleId", UUID.class)
            .column("isDefault", Boolean.class)
            .column("createdAt", Instant.class)
            .column("expiresAt", Instant.class)
            .defaultSort("createdAt DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public AccountMappingDto toDto() {
        return AccountMappingDto.builder()
                .id(id)
                .userContextId(userContextId)
                .accountId(accountId)
                .roleId(roleId)
                .isDefault(isDefault)
                .createdAt(createdAt)
                .expiresAt(expiresAt)
                .build();
    }
}