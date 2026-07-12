package com.gossamercms.media.services;

import com.gossamercms.media.data.MediaDbService;
import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.media.providers.MediaStorageProvider;
import com.gossamercms.media.storage.StorageResult;
import com.gossamercms.mvc.annotations.ModuleService;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.UUID;

@ModuleService
public class MediaService {

    private final MediaDbService mediaDbService;
    private final MediaStorageProvider storageProvider;

    public MediaService(MediaDbService mediaDbService, MediaStorageProvider storageProvider) {
        this.mediaDbService = mediaDbService;
        this.storageProvider = storageProvider;
    }

    public MediaDto upload(
            UUID userId,
            MultipartFile file
    ) {

        UUID mediaId = UUID.randomUUID();

        StorageResult storageResult =
                storageProvider.store(mediaId, file);

        MediaDto media = MediaDto.builder()
                .id(mediaId)
                .filename(file.getOriginalFilename())
                .originalFilename(file.getOriginalFilename())
                .mimeType(storageResult.getMimeType())
                .storageProvider(storageResult.getStorageProvider())
                .storageKey(storageResult.getStorageKey())
                .sizeBytes(storageResult.getSizeBytes())
                .width(storageResult.getWidth())
                .height(storageResult.getHeight())
                .createdAt(Instant.now())
                .createdBy(userId)
                .build();

        return mediaDbService.create(userId, media);
    }


}
