package com.gossamercms.rbac.services;

import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.rbac.models.RolePermission;
import com.gossamercms.rbac.dtos.RolePermissionDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class RolePermissionsDbService extends BaseDbService<RolePermission, RolePermissionDto> {

    public RolePermissionsDbService(DataSourceManager dsManager) {
        super(RolePermission.class, RolePermissionDto.class, dsManager);
    }

    @Override
    protected RolePermission mapToEntity(RolePermissionDto dto) {
        return RolePermission.builder()
                .id(dto.getId())
                .roleId(dto.getRoleId())
                .permissionId(dto.getPermissionId())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    @Override
    protected RolePermissionDto mapToDto(RolePermission entity) {
        return RolePermissionDto.builder()
                .id(entity.getId())
                .roleId(entity.getRoleId())
                .permissionId(entity.getPermissionId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    protected RolePermissionDto removeExcludedFields(RolePermissionDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("RolePermission not found: " + id);
    }

    @Override
    public ListResultset<RolePermissionDto> createOrReplaceBulk(UUID deletedBy, List<RolePermissionDto> dtos, Map<String, Object> deleteExistingKey) {
        throw new UnsupportedOperationException("Bulk role-permission creation not supported.");
    }
}