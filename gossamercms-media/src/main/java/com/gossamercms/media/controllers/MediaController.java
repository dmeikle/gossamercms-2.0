package com.gossamercms.media.controllers;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.media.handlers.MediaHandler;
import com.gossamercms.media.providers.MediaStorageProvider;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.InputStream;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("media")
public class MediaController {

    private final MediaStorageProvider storageProvider;
    private final MediaHandler mediaHandler;

    public MediaController(MediaStorageProvider storageProvider, MediaHandler mediaHandler) {
        this.storageProvider = storageProvider;
        this.mediaHandler = mediaHandler;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getMedia(
            @PathVariable UUID id
    ) {
        MediaDto media = mediaHandler.getById(id);
        InputStream stream =
                storageProvider.load(media.getStorageKey());

        InputStreamResource resource =
                new InputStreamResource(stream);

        return ResponseEntity.ok()
                .contentType(
                        MediaType.parseMediaType(
                                media.getMimeType()
                        )
                )
                .contentLength(media.getSizeBytes())
                .body(resource);
    }
}
