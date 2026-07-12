package com.gossamercms.media.data;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.media.models.Media;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class MediaDbService extends BaseDbService<Media, MediaDto> {

    public MediaDbService(DataSourceManager dsManager) {
        super(Media.class, MediaDto.class, dsManager);
    }

    public void get() {
        /*
        SELECT m.*
FROM media m
JOIN media_links ml
    ON ml."mediaId" = m."id"
WHERE ml."entityType" = 'PRODUCT'
  AND ml."entityId" = :productId
ORDER BY ml."sortOrder";
         */
    }

    @Override
    protected Media mapToEntity(MediaDto dto) {
        return dto.toEntity();
    }

    @Override
    protected MediaDto mapToDto(Media entity) {
        return entity.toDto();
    }

    @Override
    protected MediaDto removeExcludedFields(MediaDto dto) {
        return dto;
    }

    @Override
    public ListResultset<MediaDto> createOrReplaceBulk(UUID deletedBy, List<MediaDto> dtos, Map<String, Object> deleteExistingKey) {
        return null;
    }

    @Override
    protected void throw404(String id) {

    }
}
