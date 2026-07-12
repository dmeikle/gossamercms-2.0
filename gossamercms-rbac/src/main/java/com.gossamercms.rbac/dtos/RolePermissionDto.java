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
public class RolePermissionDto implements DtoWithId {
    private UUID id;
    private UUID roleId;
    private UUID permissionId;
    private Instant createdAt;
}
