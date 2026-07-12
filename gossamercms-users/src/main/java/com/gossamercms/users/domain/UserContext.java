package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.UserContextDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class UserContext implements BaseModel {

    private UUID id;
    private UUID userId;
    private String contextType;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private UUID roleId;
    private boolean isDefault;

    public static final ModelMeta META = ModelMeta.builder()
            .table("user_contexts")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("userId", UUID.class)
            .column("contextType", String.class, 50)
            .column("metadata", Map.class)
            .column("createdAt", Instant.class)
            .column("isDefault", boolean.class)
            .column("roleId", UUID.class)
            .defaultSort("createdAt DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public UserContextDto toDto() {
        return UserContextDto.builder()
                .id(id)
                .userId(userId)
                .contextType(contextType)
                .metadata(metadata)
                .createdAt(createdAt)
                .roleId(roleId)
                .isDefault(isDefault)
                .build();
    }
}