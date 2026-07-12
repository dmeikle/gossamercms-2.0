package com.gossamercms.rbac.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.rbac.models.Permission;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.services.PermissionsDbService;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ModuleHandler
public class PermissionsHandler extends BaseHandler<Permission, PermissionDto> {

    public PermissionsHandler(PermissionsDbService db) {
        super(db);
    }

    // ---------- CREATE ----------
    @Override
    public PermissionDto create(UUID createdBy, PermissionDto dto) {
        dto.setId(UUID.randomUUID());
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());
        return db.create(createdBy, dto);
    }

    // ---------- UPDATE ----------
    @Override
    public PermissionDto updateById(UUID updatedBy, UUID id, PermissionDto dto) {
        dto.setUpdatedAt(Instant.now());
        return db.updateById(updatedBy, dto, id);
    }

    @Override
    public PermissionDto update(UUID updatedBy, PermissionDto dto, Map<String, Object> params) {
        dto.setUpdatedAt(Instant.now());
        return db.update(updatedBy, dto, params);
    }
}