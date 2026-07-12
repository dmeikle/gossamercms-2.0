package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.AccountDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class Account implements BaseModel {

    private UUID id;
    private UUID organizationId;
    private String name;
    private String type;
    private Instant createdAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("accounts")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("organizationId", UUID.class)
            .column("name", String.class, 200)
            .column("type", String.class, 50)
            .column("createdAt", Instant.class)
            .defaultSort("name ASC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public AccountDto toDto() {
        return AccountDto.builder()
                .id(id)
                .organizationId(organizationId)
                .name(name)
                .type(type)
                .createdAt(createdAt)
                .build();
    }
}