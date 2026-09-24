package com.gossamercms.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    private String credentialsLocation;

    public String getCredentialsLocation() {
        return credentialsLocation;
    }

    public void setCredentialsLocation(String credentialsLocation) {
        this.credentialsLocation = credentialsLocation;
    }
}
