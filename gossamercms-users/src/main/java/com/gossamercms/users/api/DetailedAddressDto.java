package com.gossamercms.users.api;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class DetailedAddressDto implements DtoWithId {
    private UUID id;
    private UUID userId;
    private String firstname;
    private String lastname;
    private String address1;
    private String address2;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String countryCode;
    private String type;
    private boolean isDefault;
}
