package com.gossamercms.auth.controllers;

import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.PermissionDto;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.handlers.LoginIdentitiesHandler;
import com.gossamercms.auth.handlers.AuthRolePermissionsHandler;
import com.gossamercms.auth.handlers.AuthRolesHandler;
import com.gossamercms.security.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.handlers.UserContextsHandler;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/sessions")
public class SessionsController {

    private final UserContextsHandler handler;
    private final AuthRolesHandler authRolesHandler;
    private final AuthRolePermissionsHandler authRolePermissionsHandler;
    private final LoginIdentitiesHandler loginIdentitiesHandler;

    public SessionsController(UserContextsHandler handler, AuthRolesHandler authRolesHandler, AuthRolePermissionsHandler authRolePermissionsHandler, LoginIdentitiesHandler loginIdentitiesHandler) {
        this.handler = handler;
        this.authRolesHandler = authRolesHandler;
        this.authRolePermissionsHandler = authRolePermissionsHandler;
        this.loginIdentitiesHandler = loginIdentitiesHandler;
    }


    @PostMapping("/context/{userContext}")
    public Object selectContext(@CurrentUser JwtUser jwtUser, @PathVariable("userContext") UserContextDto userContext) {
        RoleDto role = this.authRolesHandler.getById(userContext.getRoleId());
        List<PermissionDto> permissions = authRolePermissionsHandler.listPermissionsForRole(role.getId());
        LoginIdentityDto identity = loginIdentitiesHandler.get(Map.of("userId",jwtUser.getUserId()));
        //JwtUser jwtUser, UserContextDto userContext, String roleName, String[] permissions, String identifier
        return  handler.selectContext(
                jwtUser,
                userContext,
                role.getName(),
                identity.getIdentifier(), permissions.stream()
                .map(PermissionDto::getName)
                .toArray(String[]::new));
    }
}
