package com.gossamercms.auth.controllers;


import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.dtos.requests.AdminRegisterRequestDto;
import com.gossamercms.auth.dtos.responses.LoginIdentityExistsResponse;
import com.gossamercms.auth.dtos.responses.RegisterResponseDto;
import com.gossamercms.auth.handlers.LoginIdentitiesHandler;
import com.gossamercms.auth.handlers.RegisterHandler;
import com.gossamercms.auth.handlers.AuthRolesHandler;
import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.users.exceptions.LoginAlreadyExistsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/auth")
public class AdminRegistrationController {

    private final RegisterHandler handler;
    private final AuthRolesHandler authRolesHandler;
    private final LoginIdentitiesHandler loginIdentitiesHandler;

    public AdminRegistrationController(
            RegisterHandler handler,
            AuthRolesHandler authRolesHandler,
            LoginIdentitiesHandler loginIdentitiesHandler) {
        this.handler = handler;
        this.authRolesHandler = authRolesHandler;
        this.loginIdentitiesHandler = loginIdentitiesHandler;
    }

    @PostMapping("/register")
    public RegisterResponseDto register(@RequestBody AdminRegisterRequestDto req) throws LoginAlreadyExistsException {
        RoleDto role = authRolesHandler.getById(req.getRoleId());
        return handler.handle(req, role);
    }

}