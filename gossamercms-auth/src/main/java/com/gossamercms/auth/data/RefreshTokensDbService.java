package com.gossamercms.auth.data;

import com.gossamercms.auth.dtos.RefreshTokenDto;
import com.gossamercms.auth.models.RefreshToken;
import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class RefreshTokensDbService  extends BaseDbService<RefreshToken, RefreshTokenDto> {

    public RefreshTokensDbService(DataSourceManager dataSourceManager) {
        super(RefreshToken.class, RefreshTokenDto.class, dataSourceManager);
    }

    @Override
    protected RefreshToken mapToEntity(RefreshTokenDto dto) {
        return dto.toEntity();
    }

    @Override
    protected RefreshTokenDto mapToDto(RefreshToken entity) {
        return entity.toDto();
    }

    @Override
    protected RefreshTokenDto removeExcludedFields(RefreshTokenDto dto) {
        return dto;
    }

    @Override
    public ListResultset<RefreshTokenDto> createOrReplaceBulk(UUID deletedBy, List<RefreshTokenDto> dtos, Map<String, Object> deleteExistingKey) {
        return null;
    }

    @Override
    protected void throw404(String id) {

    }
}
