package com.gossamercms.media.dtos;


import com.gossamercms.media.models.MediaLink;
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
public class MediaLinkDto implements DtoWithId {

    private UUID id;

    private UUID mediaId;

    private String entityType;

    private UUID entityId;

    private Integer sortOrder;

    private Boolean isPrimary;

    private Instant createdAt;

    public MediaLink toEntity() {
        return MediaLink.builder()
                .id(id)
                .mediaId(mediaId)
                .entityType(entityType)
                .entityId(entityId)
                .sortOrder(sortOrder)
                .isPrimary(Boolean.TRUE.equals(isPrimary))
                .createdAt(createdAt)
                .build();
    }

    public static MediaLinkDto fromEntity(MediaLink entity) {
        return MediaLinkDto.builder()
                .id(entity.getId())
                .mediaId(entity.getMediaId())
                .entityType(entity.getEntityType())
                .entityId(entity.getEntityId())
                .sortOrder(entity.getSortOrder())
                .isPrimary(entity.isPrimary())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
