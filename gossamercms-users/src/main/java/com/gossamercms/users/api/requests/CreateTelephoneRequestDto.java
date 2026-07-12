package com.gossamercms.users.api.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateTelephoneRequestDto {

    @NotBlank
    @Pattern(regexp = "^\\+[0-9]{1,3}$", message = "Invalid country code")
    String countryCode;

    @NotBlank
    @Pattern(
            regexp = "^[0-9\\-\\s\\(\\)]+$",
            message = "Invalid phone number format"
    )
    String number;

    @NotBlank
    @Pattern(regexp = "MOBILE|HOME|WORK|OTHER")
    String type;

    Boolean smsOptIn;
    Boolean preferred;

    @Pattern(regexp = "^[0-9]{1,6}$", message = "Invalid extension")
    String extension;
}