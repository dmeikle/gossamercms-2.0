package com.gossamercms.auth.controllers;


import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.dtos.requests.RegisterRequestDto;
import com.gossamercms.auth.dtos.responses.RegisterResponseDto;
import com.gossamercms.auth.handlers.RegisterHandler;
import com.gossamercms.auth.handlers.AuthRolesHandler;
import com.gossamercms.users.exceptions.LoginAlreadyExistsException;
import jakarta.servlet.http.HttpSession;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class RegistrationController {

    private final RegisterHandler handler;
    private final AuthRolesHandler authRolesHandler;

    public RegistrationController(RegisterHandler handler, AuthRolesHandler authRolesHandler) {
        this.handler = handler;
        this.authRolesHandler = authRolesHandler;
    }

    @Transactional
    @PostMapping("/register")
    public RegisterResponseDto register(@RequestBody RegisterRequestDto req, HttpSession session) throws LoginAlreadyExistsException {
        RoleDto role = authRolesHandler.getByName(AuthRolesHandler.DEFAULT_ROLE_NAME);
        return handler.handle(req, role, session.getId());
    }
}