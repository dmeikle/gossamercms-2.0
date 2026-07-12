package com.gossamercms.auth.dtos.responses;

import com.gossamercms.users.api.AddressDto;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserTelephoneDto;

import java.util.List;
import java.util.UUID;

public record RegisterResponseDto(
        UUID userId,
        String email,
        String firstname,
        String lastname,
        List<AddressDto> addresses,
         List<UserTelephoneDto> telephones,
        UserContextDto userContext,
        String token
) {}