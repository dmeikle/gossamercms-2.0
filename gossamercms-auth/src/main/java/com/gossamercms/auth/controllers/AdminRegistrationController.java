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
import com.gossamercms.mvc.jwt.CurrentJwtUser;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.exceptions.LoginAlreadyExistsException;
import org.springframework.transaction.annotation.Transactional;
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
    public ApiResponse<RegisterResponseDto> register(
            @CurrentUser JwtUser jwtUser,
            @RequestBody AdminRegisterRequestDto req) throws LoginAlreadyExistsException {
        System.out.println("current user: " + jwtUser.getUsername());
        RoleDto role = authRolesHandler.getById(req.getRoleId());

        return ApiResponse.ok(handler.handle(req, role));
    }

    @Transactional
    @DeleteMapping("/{user}")
    public void deleteById(
            @CurrentUser JwtUser jwtUser,
            @PathVariable("user") UserDto user
    ) {
        handler.deleteAccount(jwtUser.getUserId(), user.getId());
    }

}