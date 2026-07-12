package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.AddressDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class Address implements BaseModel {

    private UUID id;

    private UUID userId;

  //  private String type = "";

    private String address1;

    private String address2;

    private String city;

    private String stateProvince;

    private String postalCode;

    private String countryCode;

    //private Instant createdAt;

    private boolean isDefault;


    private boolean isBilling;

    public static final ModelMeta META = ModelMeta.builder()
            .table("user_addresses")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("userId", UUID.class)
          //  .column("type", String.class, 20)
            .column("address1", String.class, 255)
            .column("address2", String.class, 255)
            .column("city", String.class, 100)
            .column("stateProvince", String.class, 100)
            .column("postalCode", String.class, 30)
            .column("countryCode", String.class, 10)
           // .column("createdAt", Instant.class)
            .column("isDefault", Boolean.class)
            .column("isBilling", Boolean.class)

            .defaultSort("isDefault DESC")
            .build();

    @Override
    public ModelMeta meta() {
        return META;
    }

    public AddressDto toDto() {
        return AddressDto.builder()
                .id(id)
                .userId(userId)
//                .type(type)
                .address1(address1)
                .address2(address2)
                .city(city)
                .stateProvince(stateProvince)
                .postalCode(postalCode)
                .countryCode(countryCode)
             //   .createdAt(createdAt)
                .isDefault(isDefault)
                .isBilling(isBilling)
                .build();
    }
}
