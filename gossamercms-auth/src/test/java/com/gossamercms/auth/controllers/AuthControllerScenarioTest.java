package com.gossamercms.auth.controllers;

import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.LoginResult;
import com.gossamercms.auth.dtos.RefreshResult;
import com.gossamercms.auth.dtos.RefreshTokenDto;
import com.gossamercms.auth.dtos.requests.LoginRequestDto;
import com.gossamercms.auth.handlers.LoginHandler;
import com.gossamercms.auth.handlers.RefreshTokenHandler;
import com.gossamercms.mvc.handlers.GlobalExceptionHandler;
import com.gossamercms.tests.api.ControllerScenarioFile;
import com.gossamercms.tests.api.EndpointScenario;
import com.gossamercms.tests.api.EndpointScenarioRunner;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class AuthControllerScenarioTest {

    private static final UUID USER_ID = UUID.fromString("8f1883ef-4f30-48c5-b064-8961e4418a6f");
    private static final UUID IDENTITY_ID = UUID.fromString("11fb89dc-4b78-4fa0-b81f-ef0d922005a2");
    private static final UUID CONTEXT_ID = UUID.fromString("53b43563-dc2e-4ea4-9c52-2d59037c53de");
    private static final UUID ROLE_ID = UUID.fromString("1eeb86d9-f38d-4449-b684-b40c0bf0f1df");
    private static final UUID REFRESH_TOKEN_ID = UUID.fromString("acfecc27-cf32-4f72-bfdc-7c52f3e1cfe2");
    private static final String EMAIL = "alice@example.com";
    private static final String SESSION_ID = "session-123";
    private static final String ACCESS_TOKEN = "access-token-123";
    private static final String REFRESH_TOKEN = "refresh-token-123";

    private final LoginHandler loginHandler = mock(LoginHandler.class);
    private final RefreshTokenHandler refreshTokenHandler = mock(RefreshTokenHandler.class);
    private final EndpointScenarioRunner runner = new EndpointScenarioRunner();
    private final ControllerScenarioFile scenarioFile =
            runner.loadScenarioFile("scenarios/auth-controller-scenarios.json");
    private final MockMvc mockMvc = EndpointScenarioRunner.buildMockMvc(
            new AuthController(loginHandler, refreshTokenHandler),
            new GlobalExceptionHandler()
    );

    @TestFactory
    Stream<org.junit.jupiter.api.DynamicTest> authControllerScenarios() {
        return scenarioFile.scenarios().stream()
                .map(scenario -> dynamicTest(scenario.name(), runScenario(scenario)));
    }

    private Executable runScenario(EndpointScenario scenario) {
        return () -> {
            reset(loginHandler, refreshTokenHandler);
            arrangeScenario(scenario);
            runner.runScenario(mockMvc, scenario);
        };
    }

    private void arrangeScenario(EndpointScenario scenario) {
        switch (scenario.name()) {
            case "login happy path" -> {
                LoginResult loginResult = loginResult();
                when(loginHandler.handle(
                        eq(new LoginRequestDto(EMAIL, "super-secret")),
                        anyString()
                )).thenReturn(loginResult);
                when(refreshTokenHandler.create(loginResult)).thenReturn(refreshToken());
            }
            case "refresh happy path" -> when(refreshTokenHandler.refresh(
                    REFRESH_TOKEN,
                    SESSION_ID
            )).thenReturn(refreshResult());
            case "refresh missing token fast fail" -> {
                return;
            }
            default -> throw new IllegalArgumentException("No fixture arrangement defined for scenario " + scenario.name());
        }
    }

    private LoginResult loginResult() {
        return new LoginResult(
                user(),
                identity(),
                ACCESS_TOKEN,
                List.of(context())
        );
    }

    private RefreshResult refreshResult() {
        return new RefreshResult(
                loginResult(),
                refreshToken()
        );
    }

    private UserDto user() {
        return UserDto.builder()
                .id(USER_ID)
                .email(EMAIL)
                .firstname("Alice")
                .lastname("Example")
                .status("ACTIVE")
                .createdOn(Instant.parse("2024-01-01T00:00:00Z"))
                .build();
    }

    private LoginIdentityDto identity() {
        return LoginIdentityDto.builder()
                .id(IDENTITY_ID)
                .userId(USER_ID)
                .type("email")
                .identifier(EMAIL)
                .provider("auth0")
                .providerUserId("auth0|alice")
                .isPrimary(true)
                .createdOn(Instant.parse("2024-01-01T00:00:00Z"))
                .build();
    }

    private UserContextDto context() {
        return UserContextDto.builder()
                .id(CONTEXT_ID)
                .userId(USER_ID)
                .roleId(ROLE_ID)
                .contextType("tenant")
                .metadata(Map.of("defaultContext", true))
                .createdAt(Instant.parse("2024-01-01T00:00:00Z"))
                .build();
    }

    private RefreshTokenDto refreshToken() {
        return RefreshTokenDto.builder()
                .id(REFRESH_TOKEN_ID)
                .token(REFRESH_TOKEN)
                .username(EMAIL)
                .expiresAt(Instant.parse("2024-02-01T00:00:00Z"))
                .revoked(false)
                .build();
    }
}
