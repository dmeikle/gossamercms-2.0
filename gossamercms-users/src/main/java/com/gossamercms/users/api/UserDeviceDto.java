package com.gossamercms.users.api;

import com.gossamercms.users.domain.UserDevice;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceDto implements DtoWithId{
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

    public UserDevice toEntity() {
        return UserDevice.builder()
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
}
