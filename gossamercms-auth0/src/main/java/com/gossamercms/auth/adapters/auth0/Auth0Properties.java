package com.gossamercms.auth.adapters.auth0;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth0")
public class Auth0Properties {
    private String domain;
    private String clientId;
    private String clientSecret;

    // getters + setters
}