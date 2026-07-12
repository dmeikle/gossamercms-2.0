package com.gossamercms.users.api;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;


@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserContextDto implements DtoWithId {

    private UUID id;
    private UUID userId;
    private UUID roleId;
    private String contextType;
    private Map<String, Object> metadata;
    private Instant createdAt;
    private boolean isDefault;
}