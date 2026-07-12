package com.gossamercms.users.api;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements DtoWithId {

    private UUID id;
    private String email;
    private String firstname;
    private String lastname;
    private String status;
    private String ipAddress;
    private Instant createdOn;

    @Override
    public UUID getId() {
        return id;
    }

    public Map<String, Object> toClaims() {
        return Map.of(
                "email", email,
                "firstname", firstname,
                "lastname", lastname
        );
    }
}