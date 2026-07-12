package com.gossamercms.rbac.handlers;

import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.dtos.UserRoleDto;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.services.RolesDbService;
import com.gossamercms.rbac.services.UserRolesDbService;

import java.time.Instant;
import java.util.*;

@ModuleHandler
public class UserRolesHandler extends BaseHandler<UserRole, UserRoleDto> {

    private final RolesDbService roleDb;

    public UserRolesHandler(UserRolesDbService db, RolesDbService roleDb) {
        super(db);
        this.roleDb = roleDb;
    }

    // ------------------------------------------------------------
    // LIST ROLES FOR USER
    // ------------------------------------------------------------
    public List<RoleDto> listRolesForUser(UUID userId) {

        QueryOptions urOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("userId", userId),
                Map.of()
        );

        List<UserRoleDto> userRoles = getAll(urOpts).list();
        if (userRoles.isEmpty()) {
            return List.of();
        }

        List<UUID> roleIds = userRoles.stream()
                .map(UserRoleDto::getRoleId)
                .toList();

        QueryOptions roleOpts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of("id", roleIds),
                Map.of()
        );

        return roleDb.getAll(roleOpts).list();
    }

    // ------------------------------------------------------------
    // ASSIGN ROLE TO USER
    // ------------------------------------------------------------
    public UserRoleDto assignRoleToUser(UUID adminId, UUID userId, UUID roleId) {

        UserRoleDto dto = UserRoleDto.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .roleId(roleId)
                .assignedBy(adminId)
                .assignedAt(Instant.now())
                .build();

        return create(adminId, dto);
    }

    // ------------------------------------------------------------
    // REMOVE ROLE FROM USER
    // ------------------------------------------------------------
    public void removeRoleFromUser(UUID adminId, UUID userId, UUID roleId) {

        QueryOptions opts = new QueryOptions(
                1,
                Integer.MAX_VALUE,
                Map.of(
                        "userId", userId,
                        "roleId", roleId
                ),
                Map.of()
        );

        List<UserRoleDto> matches = getAll(opts).list();
        if (matches.isEmpty()) {
            return;
        }

        deleteById(adminId, matches.get(0).getId());
    }
}