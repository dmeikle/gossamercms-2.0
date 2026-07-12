package com.gossamercms.media.models;

import com.gossamercms.media.dtos.MediaDto;
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
public class Media implements BaseModel {

    private UUID id;

    private String filename;
    private String originalFilename;

    private String mimeType;

    private String storageProvider;
    private String storageKey;

    private Long sizeBytes;

    private Integer width;
    private Integer height;

    private
    Instant createdAt;

    private UUID createdBy;

    public static final ModelMeta META = ModelMeta.builder()
            .table("media")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("filename", String.class, 255)
            .column("originalFilename", String.class, 255)
            .column("mimeType", String.class, 100)
            .column("storageProvider", String.class, 50)
            .column("storageKey", String.class, 500)
            .column("sizeBytes", Long.class)
            .column("width", Integer.class)
            .column("height", Integer.class)
            .column("createdAt", Instant.class)
            .column("createdBy", UUID.class)
            .defaultSort("createdAt DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public MediaDto toDto() {
        return MediaDto.builder()
                .id(id)
                .filename(filename)
                .originalFilename(originalFilename)
                .mimeType(mimeType)
                .storageProvider(storageProvider)
                .storageKey(storageKey)
                .sizeBytes(sizeBytes)
                .width(width)
                .height(height)
                .createdAt(createdAt)
                .createdBy(createdBy)
                .build();
    }
}
