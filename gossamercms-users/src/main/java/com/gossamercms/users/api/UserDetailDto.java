package com.gossamercms.users.api;

import java.time.Instant;
import java.util.UUID;

public record UserDetailDto(
        UUID id,
        String firstname,
        String lastname,
        String contextType,
        Instant lastLoginAt,
        String email,
        String phoneCountryCode,
        String phoneNumber,

        String address1,
        String address2,
        String city,
        String stateProvince,
        String postalCode,
        String countryCode,
        Boolean isDefault
) {}
