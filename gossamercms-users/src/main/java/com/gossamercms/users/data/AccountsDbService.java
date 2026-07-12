package com.gossamercms.users.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.users.api.AccountDto;
import com.gossamercms.users.domain.Account;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService
public class AccountsDbService extends BaseDbService<Account, AccountDto> {

    public AccountsDbService(DataSourceManager dsManager) {
        super(Account.class, AccountDto.class, dsManager);
    }

    @Override
    protected AccountDto mapToDto(Account entity) {
        if (entity == null) return null;

        return AccountDto.builder()
                .id(entity.getId())
                .organizationId(entity.getOrganizationId())
                .name(entity.getName())
                .type(entity.getType())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    @Override
    protected Account mapToEntity(AccountDto dto) {
        if (dto == null) return null;

        return Account.builder()
                .id(dto.getId())
                .organizationId(dto.getOrganizationId())
                .name(dto.getName())
                .type(dto.getType())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Account not found: " + id);
    }

    @Override
    public ListResultset<AccountDto> createOrReplaceBulk(
            UUID deletedBy,
            List<AccountDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented yet");
    }

    @Override
    protected AccountDto removeExcludedFields(AccountDto dto) {
        return dto;
    }
}