package com.gossamercms.auth.dtos;

import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;

import java.util.List;

public record LoginResult(
        UserDto user,
        LoginIdentityDto identity,
        String accessToken,
        List<UserContextDto> contexts
) {}

