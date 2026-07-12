package com.gossamercms.rbac.handlers;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.dtos.RolePermissionDto;
import com.gossamercms.rbac.models.RolePermission;
import com.gossamercms.rbac.services.PermissionsDbService;
import com.gossamercms.rbac.services.RolePermissionsDbService;

import java.time.Instant;
import java.util.*;

@ModuleHandler
public class RolePermissionsHandler extends BaseHandler<RolePermission, RolePermissionDto> {

    private final PermissionsDbService permissionDb;

    public RolePermissionsHandler(
            RolePermissionsDbService db,
            PermissionsDbService permissionDb
    ) {
        super(db);
        this.permissionDb = permissionDb;
    }

    // ------------------------------------------------------------
    // LIST PERMISSIONS FOR ROLE
    // ------------------------------------------------------------
    public List<PermissionDto> listPermissionsForRole(UUID roleId) {

        QueryOptions rpOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("roleId", roleId),
                Map.of()
        );

        List<RolePermissionDto> rolePerms = getAll(rpOpts).list();
        if (rolePerms.isEmpty()) {
            return List.of();
        }

        List<UUID> permIds = rolePerms.stream()
                .map(RolePermissionDto::getPermissionId)
                .toList();

        QueryOptions permOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("id", permIds),
                Map.of()
        );

        return permissionDb.getAll(permOpts).list();
    }

    // ------------------------------------------------------------
    // ASSIGN PERMISSION TO ROLE
    // ------------------------------------------------------------
    public RolePermissionDto assignPermissionToRole(UUID adminId, UUID roleId, UUID permissionId) {

        RolePermissionDto dto = RolePermissionDto.builder()
                .id(UUID.randomUUID())
                .roleId(roleId)
                .permissionId(permissionId)
                .createdAt(Instant.now())
                .build();

        return create(adminId, dto);
    }

    // ------------------------------------------------------------
    // REMOVE PERMISSION FROM ROLE
    // ------------------------------------------------------------
    public void removePermissionFromRole(UUID adminId, UUID roleId, UUID permissionId) {

        QueryOptions opts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of(
                        "roleId", roleId,
                        "permissionId", permissionId
                ),
                Map.of()
        );

        List<RolePermissionDto> matches = getAll(opts).list();
        if (matches.isEmpty()) {
            return;
        }

        deleteById(adminId, matches.get(0).getId());
    }
}