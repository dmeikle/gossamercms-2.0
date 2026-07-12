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

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    protected String email;
    protected String password;

    protected String firstname;
    protected String lastname;


    // ✔ NEW: multiple telephones
    protected List<UserTelephoneDto> telephones;

    // ✔ NEW: multiple addresses (shipping, billing, default, etc.)
    protected List<AddressDto> addresses;

    protected Boolean marketingOptIn;

    protected Map<String, Object> metadata;

    protected UserContextDto userContext = UserContextDto.builder().contextType("default").build();
}