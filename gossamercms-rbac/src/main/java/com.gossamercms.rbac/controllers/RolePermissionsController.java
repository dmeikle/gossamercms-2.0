package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.RolePermissionDto;
import com.gossamercms.rbac.models.RolePermission;
import com.gossamercms.rbac.handlers.RolePermissionsHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/role-permissions")
public class RolePermissionsController extends BaseController<RolePermission, RolePermissionDto> {

    public RolePermissionsController(RolePermissionsHandler handler) {
        super(handler);
    }
}