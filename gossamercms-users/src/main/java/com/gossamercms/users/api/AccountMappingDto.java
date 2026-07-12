package com.gossamercms.users.api;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountMappingDto implements DtoWithId {

    private UUID id;
    private UUID userContextId;
    private UUID accountId;
    private UUID roleId;
    private boolean isDefault;
    private Instant createdAt;
    private Instant expiresAt;
}