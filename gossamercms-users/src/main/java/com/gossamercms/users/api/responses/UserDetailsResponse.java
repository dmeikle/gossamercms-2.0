package com.gossamercms.users.api.responses;

import com.gossamercms.users.api.*;

import java.util.List;

public record UserDetailsResponse(
        UserDetailDto userDetail,
        UserTelephoneDto telephone,
        List<AddressDto> addresses,
        List<UserContextDetailDto> contexts) {
}
