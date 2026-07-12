package com.gossamercms.auth.adapters.auth0;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gossamercms.auth.adapters.AuthResult;
import com.gossamercms.auth.adapters.AuthenticationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;
import java.util.Map;

public class Auth0AuthenticationProvider implements AuthenticationProvider {

    private static final Logger log = LoggerFactory.getLogger(Auth0AuthenticationProvider.class);

    @Value("${auth0.domain}")
    private String domain;

    @Value("${auth0.clientId}")
    private String clientId;

    @Value("${auth0.clientSecret}")
    private String clientSecret;

    @Value("${auth0.registration-audience}")
    private String registrationAudience;

    @Value("${auth0.login-audience}")
    private String loginAudience;
    public Auth0AuthenticationProvider() {}

    @Override
    public AuthResult authenticate(String username, String password) {
        try {
            String url = "https://" + domain + "/oauth/token";

            Map<String, Object> body = Map.of(
                    "grant_type", "password",
                    "client_id", clientId,
                    "client_secret", clientSecret,
                    "username", username,
                    "password", password,
                    "audience", loginAudience,
                    "scope", "openid profile email"
            );

            Map<String, Object> response = WebClient.create()
                    .post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String idToken = (String) response.get("id_token");
            if (idToken == null) {
                throw new RuntimeException("Auth0 login failed: no id_token returned");
            }

            String[] parts = idToken.split("\\.");
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

            Map<String, Object> payload = new ObjectMapper().readValue(payloadJson, Map.class);

            String sub = (String) payload.get("sub");
            String email = (String) payload.get("email");

            return new AuthResult(
                    null,
                    email,
                    Map.of(
                            "auth_provider", "auth0",
                            "provider_user_id", sub
                    )
            );

        } catch (Exception ex) {
            log.error("Auth0 authentication failed", ex);
            throw new RuntimeException("Invalid credentials", ex);
        }
    }

    @Override
    public String register(String email, String password) {
        try {
            Map<String, Object> body = Map.of(
                    "email", email,
                    "password", password,
                    "connection", "Username-Password-Authentication"
            );

            Map<String, Object> response = WebClient.create()
                    .post()
                    .uri("https://" + domain + "/api/v2/users")
                    .header("Authorization", "Bearer " + getManagementApiToken())
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return (String) response.get("user_id");

        } catch (Exception ex) {
            System.out.println("********************** ERROR WHILE REGISTERING TO AUTH0 ***************************************");
            log.error("Failed to register user in Auth0", ex);

            System.out.println("****************************************************************************************************");
            throw new RuntimeException("Failed to register user in Auth0", ex);
        }
    }

    private String getManagementApiToken() {
        Map<String, Object> body = Map.of(
                "grant_type", "client_credentials",
                "client_id", clientId,
                "client_secret", clientSecret,
                "audience", registrationAudience
        );

        Map<String, Object> response = WebClient.create()
                .post()
                .uri("https://" + domain + "/oauth/token")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        return (String) response.get("access_token");
    }
}