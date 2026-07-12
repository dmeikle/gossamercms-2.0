package com.gossamercms.auth.factories;


import com.gossamercms.auth.models.LoginIdentity;

import java.time.Instant;
import java.util.UUID;

public class LoginIdentityFactory {

    public static LoginIdentity createAuth0EmailIdentity(UUID userId, String email, String auth0UserId) {
        return LoginIdentity.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type("email")                 // still an email identity
                .identifier(email.toLowerCase()) // store the real email
                .passwordHash(null)            // Auth0 handles passwords
                .provider("auth0")
                .providerUserId(auth0UserId)   // store the Auth0 subject separately
                .isPrimary(true)
                .createdOn(Instant.now())
                .build();
    }
}
