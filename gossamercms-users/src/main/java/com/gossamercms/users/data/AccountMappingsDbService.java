package com.gossamercms.users.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.users.api.AccountMappingDto;
import com.gossamercms.users.domain.AccountMapping;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class AccountMappingsDbService extends BaseDbService<AccountMapping, AccountMappingDto> {

    public AccountMappingsDbService(DataSourceManager dsManager) {
        super(AccountMapping.class, AccountMappingDto.class, dsManager);
        System.out.println("************************* AccountMappingsDbService constructed *********************");
    }

    @Override
    protected AccountMappingDto mapToDto(AccountMapping entity) {
        if (entity == null) return null;

        return AccountMappingDto.builder()
                .id(entity.getId())
                .userContextId(entity.getUserContextId())
                .accountId(entity.getAccountId())
                .roleId(entity.getRoleId())
                .isDefault(entity.isDefault())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .build();
    }

    @Override
    protected AccountMapping mapToEntity(AccountMappingDto dto) {
        if (dto == null) return null;

        return AccountMapping.builder()
                .id(dto.getId())
                .userContextId(dto.getUserContextId())
                .accountId(dto.getAccountId())
                .roleId(dto.getRoleId())
                .isDefault(dto.isDefault())
                .createdAt(dto.getCreatedAt())
                .expiresAt(dto.getExpiresAt())
                .build();
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Account mapping not found: " + id);
    }

    @Override
    public ListResultset<AccountMappingDto> createOrReplaceBulk(
            UUID deletedBy,
            List<AccountMappingDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented yet");
    }

    @Override
    protected AccountMappingDto removeExcludedFields(AccountMappingDto dto) {
        return dto;
    }
}