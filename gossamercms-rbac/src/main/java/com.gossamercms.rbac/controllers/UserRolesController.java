package com.gossamercms.rbac.controllers;

import com.gossamercms.mvc.controllers.BaseController;
import com.gossamercms.rbac.dtos.UserRoleDto;
import com.gossamercms.rbac.models.UserRole;
import com.gossamercms.rbac.handlers.UserRolesHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rbac/user-roles")
public class UserRolesController extends BaseController<UserRole, UserRoleDto> {

    public UserRolesController(UserRolesHandler handler) {
        super(handler);
    }
}