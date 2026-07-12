package com.gossamercms.users.api;


import com.gossamercms.mvc.data.DtoWithId;
import lombok.*;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto implements DtoWithId {

    private UUID id;
    private UUID organizationId;
    private String name;
    private String type;
    private Instant createdAt;
}