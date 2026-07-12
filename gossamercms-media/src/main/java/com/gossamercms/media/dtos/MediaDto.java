package com.gossamercms.media.dtos;


import com.gossamercms.media.models.Media;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class MediaDto implements DtoWithId {

    private UUID id;

    private String filename;

    private String originalFilename;

    private String mimeType;

    private String storageProvider;

    private String storageKey;

    private Long sizeBytes;

    private Integer width;

    private Integer height;

    private Instant createdAt;

    private UUID createdBy;

    public Media toEntity() {
        return Media.builder()
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
