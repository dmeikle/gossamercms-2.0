package com.gossamercms.rbac.dtos;

import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.rbac.models.UserRole;
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
public class UserRoleDto implements DtoWithId {
    private UUID accountRoleId;
    private UUID id;
    private UUID userContextId;
    private UUID roleId;
    private UUID assignedBy;
    private Instant assignedAt;
    private Instant expiresAt;

    public UserRole toEntity() {
        return UserRole.builder()
                .id(id)
                .userContextId(userContextId)
                .roleId(roleId)
                .assignedAt(assignedAt)
                .assignedBy(assignedBy)
                .expiresAt(expiresAt)
                .build();
    }
}
