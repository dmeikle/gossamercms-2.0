package com.gossamercms.auth.adapters.auth0;


import com.gossamercms.auth.adapters.AuthenticationProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(Auth0Properties.class)
public class Auth0AutoConfig {

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new Auth0AuthenticationProvider();
    }
}