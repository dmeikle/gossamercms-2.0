package com.gossamercms.rbac.services;

import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.dtos.UserRoleDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class UserRolesDbService extends BaseDbService<UserRole, UserRoleDto> {

    public UserRolesDbService(DataSourceManager dsManager) {
        super(UserRole.class, UserRoleDto.class, dsManager);
    }

    @Override
    protected UserRole mapToEntity(UserRoleDto dto) {
        return UserRole.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .roleId(dto.getRoleId())
                .assignedBy(dto.getAssignedBy())
                .assignedAt(dto.getAssignedAt())
                .expiresAt(dto.getExpiresAt())
                .build();
    }

    @Override
    protected UserRoleDto mapToDto(UserRole entity) {
        return UserRoleDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .roleId(entity.getRoleId())
                .assignedBy(entity.getAssignedBy())
                .assignedAt(entity.getAssignedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    @Override
    protected UserRoleDto removeExcludedFields(UserRoleDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("UserRole not found: " + id);
    }

    @Override
    public ListResultset<UserRoleDto> createOrReplaceBulk(UUID deletedBy, List<UserRoleDto> dtos, Map<String, Object> deleteExistingKey) {
        throw new UnsupportedOperationException("Bulk user-role creation not supported.");
    }
}