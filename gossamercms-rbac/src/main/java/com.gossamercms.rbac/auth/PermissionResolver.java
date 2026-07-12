package com.gossamercms.rbac.auth;

import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.dtos.RolePermissionDto;
import com.gossamercms.rbac.dtos.UserRoleDto;
import com.gossamercms.rbac.services.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PermissionResolver {

    private final UserRolesDbService userRoleDb;
    private final RolePermissionsDbService rolePermissionDb;
    private final PermissionsDbService permissionDb;
    private final RolesDbService roleDb;

    public PermissionResolver(
            UserRolesDbService userRoleDb,
            RolePermissionsDbService rolePermissionDb,
            PermissionsDbService permissionDb,
            RolesDbService roleDb
    ) {
        this.userRoleDb = userRoleDb;
        this.rolePermissionDb = rolePermissionDb;
        this.permissionDb = permissionDb;
        this.roleDb = roleDb;
    }

    public Set<String> resolvePermissions(UUID userId) {

        // 1. Load user roles
        QueryOptions userRoleOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("userId", userId),
                Map.of()
        );

        List<UserRoleDto> userRoles = userRoleDb.getAll(userRoleOpts).list();
        if (userRoles.isEmpty()) {
            return Set.of();
        }

        List<UUID> roleIds = userRoles.stream()
                .map(UserRoleDto::getRoleId)
                .toList();

        // 1b. Load role names
        QueryOptions roleOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("id", roleIds),
                Map.of()
        );

        List<RoleDto> roles = roleDb.getAll(roleOpts).list();

        boolean isSuperAdmin = roles.stream()
                .anyMatch(r -> "system-admin".equalsIgnoreCase(r.getName()));

        if (isSuperAdmin) {
            return Set.of("*"); // bypass
        }

        // 2. Load role-permission mappings
        QueryOptions rpOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("roleId", roleIds),
                Map.of()
        );

        List<RolePermissionDto> rolePermissions = rolePermissionDb.getAll(rpOpts).list();
        if (rolePermissions.isEmpty()) {
            return Set.of();
        }

        List<UUID> permissionIds = rolePermissions.stream()
                .map(RolePermissionDto::getPermissionId)
                .toList();

        // 3. Load permissions
        QueryOptions permOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("id", permissionIds),
                Map.of()
        );

        List<PermissionDto> permissions = permissionDb.getAll(permOpts).list();

        return permissions.stream()
                .map(PermissionDto::getName)
                .collect(Collectors.toSet());
    }
}