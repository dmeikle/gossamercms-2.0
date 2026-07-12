package com.gossamercms.users.api;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;
import java.util.UUID;

@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDirectoryDto implements DtoWithId {
    private UUID id;
    private String email;
    private String firstname;
    private String lastname;
    private String status;
    private String ipAddress;
    private Instant createdOn;

    private String contextType;

    private Instant lastLoginAt;

    private String phoneCountryCode;
    private String phoneNumber;

    @Override
    public UUID getId() {
        return id;
    }
}