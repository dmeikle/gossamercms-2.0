package com.gossamercms.users.data;


import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.users.api.UserTelephoneDto;
import com.gossamercms.users.domain.UserTelephone;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService

public class UserTelephonesDbService extends BaseDbService<UserTelephone, UserTelephoneDto> {

    public UserTelephonesDbService(DataSourceManager dsManager) {
        super(UserTelephone.class, UserTelephoneDto.class, dsManager);
    }

    @Override
    protected UserTelephoneDto mapToDto(UserTelephone entity) {
        if (entity == null) return null;

        return UserTelephoneDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .countryCode(entity.getCountryCode())
                .numberRaw(entity.getNumberRaw())
                .type(entity.getType())
                .verified(entity.isVerified())
                .smsOptIn(entity.isSmsOptIn())
                .preferred(entity.isPreferred())
                .extension(entity.getExtension())
                .createdOn(entity.getCreatedOn())
                .build();
    }

    @Override
    protected UserTelephone mapToEntity(UserTelephoneDto dto) {
        if (dto == null) return null;

        return UserTelephone.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .countryCode(dto.getCountryCode())
                .numberRaw(dto.getNumberRaw())
                .numberE164(normalize(dto.getCountryCode(), dto.getNumberRaw()))
                .type(dto.getType())
                .verified(dto.isVerified())
                .smsOptIn(dto.isSmsOptIn())
                .preferred(dto.isPreferred())
                .extension(dto.getExtension())
                .createdOn(dto.getCreatedOn())
                .build();
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("User telephone not found: " + id);
    }

    @Override
    public ListResultset<UserTelephoneDto> createOrReplaceBulk(
            UUID deletedBy,
            List<UserTelephoneDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented yet");
    }

    @Override
    protected UserTelephoneDto removeExcludedFields(UserTelephoneDto dto) {
        return dto;
    }

    // Internal normalization helper (same logic as domain)
    private static String normalize(String countryCode, String number) {
        if (countryCode == null || number == null) return null;
        String digits = number.replaceAll("[^0-9]", "");
        return countryCode + digits;
    }
}
