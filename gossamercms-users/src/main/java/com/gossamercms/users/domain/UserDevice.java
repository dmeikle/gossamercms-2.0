package com.gossamercms.users.domain;

import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDevice implements BaseModel{
    private UUID id;
    private UUID userId;
    private String firebaseToken;
    private String platform;
    private String deviceId;
    private String appVersion;
    private Boolean active;
    private Instant lastSeenAt;
    private Instant createdAt;
    private Instant updatedAt;

    public UserDeviceDto toDto() {
        return UserDeviceDto.builder()
            .id(id)
            .userId(userId)
            .firebaseToken(firebaseToken)
            .platform(platform)
            .deviceId(deviceId)
            .appVersion(appVersion)
            .active(active)
            .lastSeenAt(lastSeenAt)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }


public static final ModelMeta META = ModelMeta.builderWithId("user_devices")
    .column("id", UUID.class)
    .column("userId", UUID.class)
    .column("firebaseToken", String.class)
    .column("platform", String.class)
    .column("deviceId", String.class)
    .column("appVersion", String.class)
    .column("active", Boolean.class)
    .column("lastSeenAt", Instant.class)
    .column("createdAt", Instant.class)
    .column("updatedAt", Instant.class)
    .defaultSort("id desc")
    .build();



    @Override
    public ModelMeta meta() {
        return META;
    }

    @Override
    public ModelMeta metaOf() {
        return BaseModel.super.metaOf();
    }

}
