package com.gossamercms.rbac.services;

import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.rbac.models.Permission;
import com.gossamercms.rbac.dtos.PermissionDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PermissionsDbService extends BaseDbService<Permission, PermissionDto> {

    public PermissionsDbService(DataSourceManager dsManager) {
        super(Permission.class, PermissionDto.class, dsManager);
    }

    @Override
    protected Permission mapToEntity(PermissionDto dto) {
        return Permission.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    @Override
    protected PermissionDto mapToDto(Permission entity) {
        return PermissionDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected PermissionDto removeExcludedFields(PermissionDto dto) {
        return dto;
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Permission not found: " + id);
    }

    @Override
    public ListResultset<PermissionDto> createOrReplaceBulk(UUID deletedBy, List<PermissionDto> dtos, Map<String, Object> deleteExistingKey) {
        throw new UnsupportedOperationException("Bulk permission creation not supported.");
    }
}