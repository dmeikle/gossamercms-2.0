package com.gossamercms.media.data;

import com.gossamercms.media.dtos.MediaDto;
import com.gossamercms.media.dtos.MediaLinkDto;
import com.gossamercms.media.models.Media;
import com.gossamercms.media.models.MediaLink;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class MediaLinksDbService  extends BaseDbService<MediaLink, MediaLinkDto> {

    public MediaLinksDbService(DataSourceManager dsManager) {
        super(MediaLink.class, MediaLinkDto.class, dsManager);
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
    protected MediaLink mapToEntity(MediaLinkDto dto) {
        return dto.toEntity();
    }

    @Override
    protected MediaLinkDto mapToDto(MediaLink entity) {
        return entity.toDto();
    }

    @Override
    protected MediaLinkDto removeExcludedFields(MediaLinkDto dto) {
        return null;
    }

    @Override
    public ListResultset<MediaLinkDto> createOrReplaceBulk(UUID deletedBy, List<MediaLinkDto> dtos, Map<String, Object> deleteExistingKey) {
        return null;
    }

    @Override
    protected void throw404(String id) {

    }
}
