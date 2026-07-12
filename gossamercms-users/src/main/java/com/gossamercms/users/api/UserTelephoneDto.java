package com.gossamercms.users.api;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class UserTelephoneDto implements DtoWithId {
    private UUID id;
    private UUID userId;
    private String countryCode;
    private String numberRaw;
    private String type;
    private boolean verified;
    private boolean smsOptIn;
    private boolean preferred;
    private String extension;
    private Instant createdOn;
}