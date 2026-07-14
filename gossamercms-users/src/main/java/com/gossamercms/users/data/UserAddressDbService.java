package com.gossamercms.users.data;

import com.gossamercms.mvc.annotations.ModuleDbService;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DataSourceManager;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.users.api.AddressDto;
import com.gossamercms.users.domain.Address;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ModuleDbService

public class UserAddressDbService extends BaseDbService<Address, AddressDto> {

    public UserAddressDbService(DataSourceManager dsManager) {
        super(Address.class, AddressDto.class, dsManager);
    }

    // -----------------------------
    //  Mapping
    // -----------------------------

    @Override
    protected Address mapToEntity(AddressDto dto) {
        return dto.toEntity();
    }


    @Override
    protected AddressDto mapToDto(Address entity) {
        return entity.toDto();
    }


    @Override
    protected AddressDto removeExcludedFields(AddressDto dto) {
        // No excluded fields for addresses
        return dto;
    }

    // -----------------------------
    //  Bulk Replace (not used yet)
    // -----------------------------

    @Override
    public ListResultset<AddressDto> createOrReplaceBulk(
            UUID deletedBy,
            List<AddressDto> dtos,
            Map<String, Object> deleteExistingKey
    ) {
        throw new UnsupportedOperationException("Bulk replace not implemented for addresses");
    }

    // -----------------------------
    //  404 Handler
    // -----------------------------

    @Override
    protected void throw404(String id) {
        throw new RuntimeException("Address not found: " + id);
    }
}