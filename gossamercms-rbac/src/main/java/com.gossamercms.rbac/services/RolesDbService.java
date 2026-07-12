package com.gossamercms.rbac.services;


import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.rbac.models.Role;
import com.gossamercms.rbac.dtos.RoleDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service("rbacRolesDbService")
public class RolesDbService extends BaseDbService<Role, RoleDto> {

    public RolesDbService(DataSourceManager dsManager) {
        super(Role.class, RoleDto.class, dsManager);
    }

    @Override
    protected Role mapToEntity(RoleDto dto) {
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .isSystem(dto.isSystem())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Override
    protected RoleDto mapToDto(Role entity) {
        return RoleDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .isSystem(entity.isSystem())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected RoleDto removeExcludedFields(RoleDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Role not found: " + id);
    }

    @Override
    public ListResultset<RoleDto> createOrReplaceBulk(UUID deletedBy, List<RoleDto> dtos, Map<String, Object> deleteExistingKey) {
        throw new UnsupportedOperationException("Bulk role creation not supported.");
    }
}
