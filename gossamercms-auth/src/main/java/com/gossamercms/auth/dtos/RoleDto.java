package com.gossamercms.auth.dtos;

import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto implements DtoWithId {
    private UUID id;
    private String name;
    private String description;
    private boolean isSystem;
    private Instant createdAt;
    private Instant updatedAt;
}