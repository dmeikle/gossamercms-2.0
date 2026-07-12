package com.gossamercms.media.storage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StorageResult {

    private String storageProvider;

    private String storageKey;

    private String url;

    private Long sizeBytes;

    private Integer width;

    private Integer height;

    private String mimeType;
}