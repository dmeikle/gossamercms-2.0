package com.gossamercms.auth.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.models.Role;
import com.gossamercms.auth.data.RolesDbService;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ModuleHandler
public class AuthRolesHandler extends BaseHandler<Role, RoleDto> {

    public final static String DEFAULT_ROLE_NAME = "viewer";

    public AuthRolesHandler(RolesDbService db) {
        super(db);
    }

    // ---------- CREATE ----------
    @Override
    public RoleDto create(UUID createdBy, RoleDto dto) {
        dto.setId(UUID.randomUUID());
        dto.setCreatedAt(Instant.now());
        dto.setUpdatedAt(Instant.now());
        return db.create(createdBy, dto);
    }

    // ---------- UPDATE ----------
    @Override
    public RoleDto updateById(UUID updatedBy, UUID id, RoleDto dto) {
        dto.setUpdatedAt(Instant.now());
        return db.updateById(updatedBy, dto, id);
    }

    @Override
    public RoleDto update(UUID updatedBy, RoleDto dto, Map<String, Object> params) {
        dto.setUpdatedAt(Instant.now());
        return db.update(updatedBy, dto, params);
    }

    public RoleDto getByName(String name) {
        return db.get(Map.of("name", name));
    }
}