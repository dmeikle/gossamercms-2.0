package com.gossamercms.auth.dtos.responses;

import com.gossamercms.users.api.AddressDto;
import com.gossamercms.users.api.UserTelephoneDto;

import java.util.List;
import java.util.UUID;

public record AdminRegisterResponseDto(
        UUID userId,
        String email,
        String firstname,
        String lastname,
        List<AddressDto> addresses,
        List<UserTelephoneDto> telephones,
        String token

) {}