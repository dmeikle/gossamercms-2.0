package com.gossamercms.users.api;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class UserContextDetailDto {
    private UUID id;
    private UUID userId;
    private String contextType;
    private JsonNode metadata;
    private UUID roleId;
    private String name;
    private String description;
    private boolean isSystem;
}
