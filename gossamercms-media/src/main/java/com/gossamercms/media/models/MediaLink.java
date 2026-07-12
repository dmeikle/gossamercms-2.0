package com.gossamercms.media.models;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.media.dtos.MediaLinkDto;
import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class MediaLink implements BaseModel {

    private UUID id;

    private UUID mediaId;

    private String entityType;

    private UUID entityId;

    private Integer sortOrder;

    private boolean isPrimary;

    private Instant createdAt;

    public static final ModelMeta META = ModelMeta.builder()
            .table("media_links")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("mediaId", UUID.class)
            .column("entityType", String.class, 100)
            .column("entityId", UUID.class)
            .column("sortOrder", Integer.class)
            .column("isPrimary", Boolean.class)
            .column("createdAt", Instant.class)
            .defaultSort("sortOrder ASC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }


    public MediaLinkDto toDto() {
        return MediaLinkDto.builder()
                .id(id)
                .mediaId(mediaId)
                .entityType(entityType)
                .entityId(entityId)
                .sortOrder(sortOrder)
                .isPrimary(isPrimary)
                .createdAt(createdAt)
                .build();
    }
}
