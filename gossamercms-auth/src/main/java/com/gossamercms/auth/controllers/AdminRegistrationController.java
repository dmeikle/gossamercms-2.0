package com.gossamercms.auth.controllers;


import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.dtos.requests.AdminRegisterRequestDto;
import com.gossamercms.auth.dtos.responses.RegisterResponseDto;
import com.gossamercms.auth.handlers.RegisterHandler;
import com.gossamercms.auth.handlers.AuthRolesHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
public class AdminRegistrationController {

    private final RegisterHandler handler;
    private final AuthRolesHandler authRolesHandler;

    public AdminRegistrationController(RegisterHandler handler, AuthRolesHandler authRolesHandler) {
        this.handler = handler;
        this.authRolesHandler = authRolesHandler;
    }

    @PostMapping("/register")
    public RegisterResponseDto register(@RequestBody AdminRegisterRequestDto req) {
        RoleDto role = authRolesHandler.getById(req.getRoleId());
        return handler.handle(req, role);
    }
}