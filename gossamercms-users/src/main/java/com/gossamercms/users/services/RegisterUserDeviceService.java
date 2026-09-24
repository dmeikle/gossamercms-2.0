package com.gossamercms.users.services;

import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.mvc.exceptions.NotFoundException;
import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.users.api.requests.RegisterUserDeviceRequest;
import com.gossamercms.users.data.UserDevicesDbService;
import com.gossamercms.users.enums.DevicePlatform;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@ModuleService
@RequiredArgsConstructor
public class RegisterUserDeviceService {

    private final UserDevicesDbService userDevicesDbService;

    @Transactional
    public void register(
            UUID authenticatedUserId,
            UserDeviceDto device,
            RegisterUserDeviceRequest request) {

        device.setUserId(authenticatedUserId);
        device.setFirebaseToken(request.getFirebaseToken());
        device.setPlatform(
                            String.valueOf(DevicePlatform.valueOf(
                                    request.getPlatform().toUpperCase()
                            ))
                    );
        device.setAppVersion(request.getAppVersion());
        device.setActive(true);
        device.setLastSeenAt(Instant.now());


        userDevicesDbService.create(authenticatedUserId, device);

    }
}
