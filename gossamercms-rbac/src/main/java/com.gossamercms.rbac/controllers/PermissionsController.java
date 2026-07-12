package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.PermissionDto;
import com.gossamercms.rbac.models.Permission;
import com.gossamercms.rbac.handlers.PermissionsHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/permissions")
public class PermissionsController extends BaseController<Permission, PermissionDto> {

    public PermissionsController(PermissionsHandler handler) {
        super(handler);
    }
}