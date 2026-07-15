package com.gossamercms.auth.controllers;


import com.gossamercms.auth.dtos.LoginResult;
import com.gossamercms.auth.dtos.RefreshResult;
import com.gossamercms.auth.dtos.RefreshTokenDto;
import com.gossamercms.auth.dtos.requests.LoginRequestDto;
import com.gossamercms.auth.dtos.requests.RefreshTokenRequest;
import com.gossamercms.auth.dtos.responses.LoginResponseDto;
import com.gossamercms.auth.handlers.LoginHandler;
import com.gossamercms.auth.handlers.RefreshTokenHandler;
import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final LoginHandler loginHandler;
    private final RefreshTokenHandler refreshTokenHandler;

    public AuthController(LoginHandler loginHandler, RefreshTokenHandler refreshTokenHandler) {
        this.loginHandler = loginHandler;
        this.refreshTokenHandler = refreshTokenHandler;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto req, HttpSession session) {
        LoginResult loginResult =  loginHandler.handle(req, session.getId());
        RefreshTokenDto refreshToken = refreshTokenHandler.create(loginResult);

        return new LoginResponseDto(loginResult.user().getId(),
                loginResult.identity().getIdentifier(),
                loginResult.user().getFirstname(),
                loginResult.user().getLastname(),
                loginResult.accessToken(),
                refreshToken.getToken(),
                loginResult.contexts());
    }

    @PostMapping("/refresh")
    public LoginResponseDto refresh(
            @CurrentUser JwtUser jwtUser,
            @RequestBody RefreshTokenRequest request) throws BadRequestException {

        if (request == null || request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new BadRequestException("Refresh token is required");
        }

        String sessionId = (jwtUser == null)
                ? UUID.randomUUID().toString()
                : jwtUser.getSessionId();

        RefreshResult refreshResult =
                refreshTokenHandler.refresh(request.refreshToken(), sessionId);

        return new LoginResponseDto(
                refreshResult.loginResult().user().getId(),
                refreshResult.loginResult().identity().getIdentifier(),
                refreshResult.loginResult().user().getFirstname(),
                refreshResult.loginResult().user().getLastname(),
                refreshResult.loginResult().accessToken(),
                refreshResult.refreshToken().getToken(),
                refreshResult.loginResult().contexts());
    }
}
