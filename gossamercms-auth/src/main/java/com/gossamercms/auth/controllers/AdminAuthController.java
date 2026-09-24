package com.gossamercms.auth.controllers;

import com.gossamercms.auth.dtos.LoginResult;
import com.gossamercms.auth.dtos.RefreshTokenDto;
import com.gossamercms.auth.dtos.responses.LoginResponseDto;
import com.gossamercms.auth.handlers.MasqueradeHandler;
import com.gossamercms.auth.handlers.RefreshTokenHandler;
import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.security.jwt.JwtUser;
import com.gossamercms.users.api.UserDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * AdminAuthController
 *
 * Generally, all authorization/login is handlined in the regular AuthController.
 *
 * This AdminAuthController is for specific Admin only features such as:
 * start-masquerade
 * stop-masquerade
 */
@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final MasqueradeHandler masqueradeHandler;
    private final RefreshTokenHandler refreshTokenHandler;

    @PostMapping("/start-masquerading/{user}")
    public LoginResponseDto startMasquerading(
            @CurrentUser JwtUser jwtUser,
            @PathVariable("user") UserDto user,
            HttpSession session
    ) {
        LoginResult loginResult = masqueradeHandler.start(jwtUser, user, session.getId());
        RefreshTokenDto refreshToken = refreshTokenHandler.create(loginResult);

        return toResponse(loginResult, refreshToken);
    }

    @PostMapping("/stop-masquerading")
    public LoginResponseDto stopMasquerading(@CurrentUser JwtUser jwtUser, HttpSession session) {
        LoginResult loginResult = masqueradeHandler.stop(jwtUser, session.getId());
        RefreshTokenDto refreshToken = refreshTokenHandler.create(loginResult);

        return toResponse(loginResult, refreshToken);
    }

    private LoginResponseDto toResponse(LoginResult loginResult, RefreshTokenDto refreshToken) {
        return new LoginResponseDto(
                loginResult.user().getId(),
                loginResult.identity().getIdentifier(),
                loginResult.user().getFirstname(),
                loginResult.user().getLastname(),
                loginResult.accessToken(),
                refreshToken.getToken(),
                loginResult.contexts()
        );
    }
}
