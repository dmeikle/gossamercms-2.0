package com.gossamercms.auth.adapters;


public interface AuthenticationProvider {
    AuthResult authenticate(String username, String password);

    /**
     * Creates a new external identity (Auth0 user, DB user, LDAP entry, etc.)
     * Returns the provider_user_id (e.g. "auth0|abc123")
     */
    String register(String email, String password);
}