package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.DetailedAddressDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class DetailedAddress implements BaseModel {

    private UUID id;

    private UUID userId;

    private String type;

    private String firstname;
    private String lastname;

    private String company;

    private String address1;
    private String address2;

    private String city;

    private String stateProvince;

    private String postalCode;

    private String countryCode;

    private Instant createdAt;

    private boolean isDefault;

    public static final ModelMeta META = ModelMeta.builder()
//            .table("purchase_addresses")
//            .datasource("postgres")
            .column("id", UUID.class)
            .column("userId", UUID.class)
            .column("type", String.class, 20)
            .column("firstname", String.class, 100)
            .column("lastname", String.class, 100)
            .column("company", String.class, 255)
            .column("address1", String.class, 255)
            .column("address2", String.class, 255)
            .column("city", String.class, 100)
            .column("stateProvince", String.class, 100)
            .column("postalCode", String.class, 30)
            .column("countryCode", String.class, 10)
            .column("createdAt", Instant.class)
            .column("isDefault", Boolean.class)
            .defaultSort("createdAt DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public DetailedAddressDto toDto() {
        return DetailedAddressDto.builder()
                .id(id)
                .userId(userId)
                .type(type)
                .firstname(firstname)
                .lastname(lastname)
                .address1(address1)
                .address2(address2)
                .city(city)
                .stateProvince(stateProvince)
                .postalCode(postalCode)
                .countryCode(countryCode)
                .isDefault(isDefault)
                .build();
    }
}