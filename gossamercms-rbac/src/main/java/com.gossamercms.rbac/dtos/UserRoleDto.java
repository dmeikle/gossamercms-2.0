package com.gossamercms.rbac.dtos;

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
public class UserRoleDto implements DtoWithId {
    private UUID accountRoleId;
    private UUID id;
    private UUID userId;
    private UUID roleId;
    private UUID assignedBy;
    private Instant assignedAt;
    private Instant expiresAt;
}
