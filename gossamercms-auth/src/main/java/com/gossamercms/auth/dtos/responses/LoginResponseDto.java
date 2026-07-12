package com.gossamercms.auth.dtos.responses;

import com.gossamercms.users.api.UserContextDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoginResponseDto {
    private UUID userId;
    private String email;
    private String firstname;
    private String lastname;
    private String token;
    private String refreshToken;
    private List<UserContextDto> contexts;
}

