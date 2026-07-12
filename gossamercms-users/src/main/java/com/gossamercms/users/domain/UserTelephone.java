package com.gossamercms.users.domain;

import com.gossamercms.mvc.annotations.ModuleModel;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.users.api.UserTelephoneDto;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@ModuleModel
@Getter
@Builder(toBuilder = true)
public class UserTelephone implements BaseModel {



    // ⭐ Stateful fields
    private UUID id;
    private UUID userId;

    private String countryCode;   // +1
    private String numberRaw;     // "604-555-0199"
    private String numberE164;    // "+16045550199"

    private String type;          // MOBILE, HOME, WORK, OTHER

    private boolean verified;
    private boolean smsOptIn;
    private boolean preferred;

    private String extension;     // optional
    private Instant createdOn;

    // ⭐ Required static metadata
    public static final ModelMeta META = ModelMeta.builder()
            .table("user_telephone")
            .datasource("postgres")
            .column("id", UUID.class)
            .column("userId", UUID.class)
            .column("countryCode", String.class, 3)
            .column("numberRaw", String.class, 15)
            .column("numberE164", String.class, 15)
            .column("type", String.class, 20)
            .column("verified", Boolean.class)
            .column("smsOptIn", Boolean.class)
            .column("preferred", Boolean.class)
            .column("extension", String.class, 10)
            .column("createdOn", Instant.class)
            .defaultSort("createdOn DESC")
            .build();


    @Override
    public ModelMeta meta() {
        return META;
    }

    // ⭐ Factory method
    public static UserTelephone createNew(
            UUID userId,
            String countryCode,
            String numberRaw,
            String type,
            boolean smsOptIn,
            boolean preferred,
            String extension
    ) {
        return UserTelephone.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .countryCode(countryCode)
                .numberRaw(numberRaw)
                .numberE164(normalize(countryCode, numberRaw))
                .type(type)
                .verified(false)
                .smsOptIn(smsOptIn)
                .preferred(preferred)
                .extension(extension)
                .createdOn(Instant.now())
                .build();
    }

    // ⭐ Convert domain → DTO
    public UserTelephoneDto toDto() {
        return UserTelephoneDto.builder()
                .id(id)
                .userId(userId)
                .countryCode(countryCode)
                .numberRaw(numberRaw)
                .type(type)
                .verified(verified)
                .smsOptIn(smsOptIn)
                .preferred(preferred)
                .extension(extension)
                .createdOn(createdOn)
                .build();
    }

    // ⭐ Internal normalization helper
    private static String normalize(String countryCode, String number) {
        String digits = number.replaceAll("[^0-9]", "");
        return countryCode + digits;
    }
}