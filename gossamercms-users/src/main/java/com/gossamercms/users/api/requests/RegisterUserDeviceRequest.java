package com.gossamercms.users.api.requests;

import lombok.Data;

@Data
public class RegisterUserDeviceRequest {
    private String firebaseToken;
    private String platform;
    private String appVersion;

}
