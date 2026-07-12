package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.RoleDto;
import com.gossamercms.rbac.models.Role;
import com.gossamercms.rbac.handlers.RolesHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/roles")
public class RolesController extends BaseController<Role, RoleDto> {

    public RolesController(RolesHandler handler) {
        super(handler);
    }
}