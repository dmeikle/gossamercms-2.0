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
        if (dto == null) return null;

        return Address.builder()
                .id(dto.getId() != null ? dto.getId() : UUID.randomUUID())
                .userId(dto.getUserId())
                .address1(dto.getAddress1())
                .address2(dto.getAddress2())
                .city(dto.getCity())
                .stateProvince(dto.getStateProvince())
                .postalCode(dto.getPostalCode())
                .countryCode(dto.getCountryCode())
                .isDefault(dto.isDefault())
                .build();
    }


    @Override
    protected AddressDto mapToDto(Address entity) {
        if (entity == null) return null;

        return AddressDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .address1(entity.getAddress1())
                .address2(entity.getAddress2())
                .city(entity.getCity())
                .stateProvince(entity.getStateProvince())
                .postalCode(entity.getPostalCode())
                .countryCode(entity.getCountryCode())
                .isDefault(entity.isDefault())
                .build();
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