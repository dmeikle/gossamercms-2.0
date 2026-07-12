package com.gossamercms.users.api.responses;

import com.gossamercms.security.jwt.TokenResponse;

import java.util.UUID;

public record SwitchContextResponseDto (
        UUID userId,
        UUID contextId,
        String contextType,
//        String identifier,
//        UUID accountId,
//        UUID roleId,
        String token){

}

