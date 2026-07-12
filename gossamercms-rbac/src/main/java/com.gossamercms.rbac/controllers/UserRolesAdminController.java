package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.dtos.UserRoleDto;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.handlers.UserRolesHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/rbac/users")
public class UserRolesAdminController extends BaseController<UserRole, UserRoleDto> {

    private final UserRolesHandler handler;

    public UserRolesAdminController(UserRolesHandler handler) {
        super(handler);
        this.handler = handler;
    }

    // ------------------------------------------------------------
    // GET: List roles for a user
    // ------------------------------------------------------------
    @GetMapping("/{userId}/roles")
    public List<RoleDto> listRolesForUser(@PathVariable UUID userId) {
        return handler.listRolesForUser(userId);
    }

    // ------------------------------------------------------------
    // POST: Assign role to user
    // ------------------------------------------------------------
    @PostMapping("/{userId}/roles/{roleId}")
    public UserRoleDto assignRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        return handler.assignRoleToUser(adminId, userId, roleId);
    }

    // ------------------------------------------------------------
    // DELETE: Remove role from user
    // ------------------------------------------------------------
    @DeleteMapping("/{userId}/roles/{roleId}")
    public void removeRole(
            @PathVariable UUID userId,
            @PathVariable UUID roleId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        handler.removeRoleFromUser(adminId, userId, roleId);
    }
}