package com.gossamercms.media.providers;


import com.gossamercms.media.storage.StorageResult;
import com.gossamercms.mvc.annotations.ModuleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@ModuleProvider
public class LocalMediaStorageProvider implements MediaStorageProvider {

    private final Path rootDirectory;

    public LocalMediaStorageProvider(
            @Value("${media.storage.path}") String storagePath
    ) {
        System.out.println("Initialize LocalMediaStorageProvider *******************************");
        this.rootDirectory = Paths.get(storagePath);
    }

    @Override
    public StorageResult store(UUID mediaId, MultipartFile file) {

        try {
            Files.createDirectories(rootDirectory);

            String extension = getExtension(file.getOriginalFilename());

            String storageKey = extension.isBlank()
                    ? mediaId.toString()
                    : mediaId + "." + extension;

            Path destination = rootDirectory.resolve(storageKey);

            Files.copy(
                    file.getInputStream(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return StorageResult.builder()
                    .storageProvider("local")
                    .storageKey(storageKey)
                    .url("/media/" + storageKey)
                    .sizeBytes(file.getSize())
                    .mimeType(file.getContentType())
                    .build();

        } catch (IOException e) {
            throw new RuntimeException("Failed to store media", e);
        }
    }

    @Override
    public InputStream load(String storageKey) {

        try {
            return Files.newInputStream(
                    rootDirectory.resolve(storageKey)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to load media", e);
        }
    }

    @Override
    public void delete(String storageKey) {

        try {
            Files.deleteIfExists(
                    rootDirectory.resolve(storageKey)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete media", e);
        }
    }

    private String getExtension(String filename) {

        if (filename == null) {
            return "";
        }

        int dotIndex = filename.lastIndexOf('.');

        if (dotIndex < 0) {
            return "";
        }

        return filename.substring(dotIndex + 1);
    }
}
