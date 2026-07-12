package com.gossamercms.auth.dtos.requests;

public record LoginRequestDto(
        String email,
        String password
) {}
