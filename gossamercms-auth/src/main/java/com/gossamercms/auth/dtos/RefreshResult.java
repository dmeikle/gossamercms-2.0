package com.gossamercms.auth.dtos;

public record RefreshResult(
    LoginResult loginResult,
    RefreshTokenDto refreshToken
) {
}
