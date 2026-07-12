package com.gossamercms.auth.dtos.requests;

import com.gossamercms.users.api.AddressDto;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserTelephoneDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterRequestDto {

    private String email;
    private String password;

    private String firstname;
    private String lastname;
    private UUID roleId;

    // ✔ NEW: multiple telephones
    private List<UserTelephoneDto> telephones;

    // ✔ NEW: multiple addresses (shipping, billing, default, etc.)
    private List<AddressDto> addresses;

    private Boolean marketingOptIn;

    private Map<String, Object> metadata;

    private UserContextDto userContext;
}