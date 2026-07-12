package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.dtos.RolePermissionDto;
import com.gossamercms.rbac.models.RolePermission;
import com.gossamercms.rbac.handlers.RolePermissionsHandler;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin/rbac/roles")
public class RolePermissionsAdminController extends BaseController<RolePermission, RolePermissionDto> {

    private final RolePermissionsHandler handler;

    public RolePermissionsAdminController(RolePermissionsHandler handler) {
        super(handler);
        this.handler = handler;
    }

    // ------------------------------------------------------------
    // GET: List permissions for a role
    // ------------------------------------------------------------
    @GetMapping("/{roleId}/permissions")
    public List<PermissionDto> listPermissionsForRole(@PathVariable UUID roleId) {
        return handler.listPermissionsForRole(roleId);
    }

    // ------------------------------------------------------------
    // POST: Assign permission to role
    // ------------------------------------------------------------
    @PostMapping("/{roleId}/permissions/{permissionId}")
    public RolePermissionDto assignPermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        return handler.assignPermissionToRole(adminId, roleId, permissionId);
    }

    // ------------------------------------------------------------
    // DELETE: Remove permission from role
    // ------------------------------------------------------------
    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    public void removePermission(
            @PathVariable UUID roleId,
            @PathVariable UUID permissionId,
            @RequestAttribute("authUserId") UUID adminId
    ) {
        handler.removePermissionFromRole(adminId, roleId, permissionId);
    }
}