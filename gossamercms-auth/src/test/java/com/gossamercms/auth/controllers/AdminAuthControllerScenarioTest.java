package com.gossamercms.auth.controllers;

import com.gossamercms.auth.dtos.LoginResult;
import com.gossamercms.auth.dtos.RefreshTokenDto;
import com.gossamercms.auth.handlers.MasqueradeHandler;
import com.gossamercms.auth.handlers.RefreshTokenHandler;
import com.gossamercms.mvc.exceptions.UnauthorizedStatusException;
import com.gossamercms.mvc.handlers.GlobalExceptionHandler;
import com.gossamercms.tests.api.ControllerScenarioFile;
import com.gossamercms.tests.api.EndpointScenario;
import com.gossamercms.tests.api.EndpointScenarioRunner;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.converters.UserConverter;
import com.gossamercms.users.data.UsersDbService;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminAuthControllerScenarioTest {

    private static final UUID REFRESH_TOKEN_ID = UUID.fromString("acfecc27-cf32-4f72-bfdc-7c52f3e1cfe2");
    private static final String EMAIL = "alice@example.com";
    private static final String SESSION_ID = "session-123";
    private static final String ACCESS_TOKEN = "access-token-123";
    private static final String REFRESH_TOKEN = "refresh-token-123";

    private final MasqueradeHandler masqueradeHandler = mock(MasqueradeHandler.class);
    private final RefreshTokenHandler refreshTokenHandler = mock(RefreshTokenHandler.class);
    private final UsersDbService usersDbService = mock(UsersDbService.class);

    private final EndpointScenarioRunner runner = new EndpointScenarioRunner();
    private final ControllerScenarioFile scenarioFile =
            runner.loadScenarioFile("scenarios/admin-auth-controller-scenarios.json");

    private final MockMvc mockMvc = buildMockMvc();

    private MockMvc buildMockMvc() {
        var conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new UserConverter(usersDbService));

        return MockMvcBuilders.standaloneSetup(
                        new AdminAuthController(masqueradeHandler, refreshTokenHandler)
                )
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new com.gossamercms.tests.api.CurrentUserArgumentResolver())
                .setConversionService(conversionService)
                .build();
    }
//
//    @TestFactory
//    Stream<DynamicTest> adminAuthScenarios() {
//        return scenarioFile.scenarios().stream()
//                .map(s -> dynamicTest(s.name(), () -> {
//                    reset(masqueradeHandler, refreshTokenHandler, usersDbService);
//                    arrangeScenario(s);
//                    runner.runScenario(mockMvc, s);
//                }));
//    }

//    private void arrangeScenario(EndpointScenario scenario) {
//        switch (scenario.name()) {
//            case "start masquerading happy path" -> {
//                UserDto targetUser = targetUser();
//                LoginResult loginResult = masqueradeLoginResult();
//
//                when(usersDbService.getById(targetUser.getId())).thenReturn(targetUser);
//                when(masqueradeHandler.start(any(), eq(targetUser), anyString())).thenReturn(loginResult);
//                when(refreshTokenHandler.create(loginResult)).thenReturn(refreshToken());
//            }
//
//            case "stop masquerading happy path" -> {
//                LoginResult loginResult = adminLoginResult();
//
//                when(masqueradeHandler.stop(any(), anyString())).thenReturn(loginResult);
//                when(refreshTokenHandler.create(loginResult)).thenReturn(refreshToken());
//            }
//
//            case "stop masquerading unauthorized" -> {
//                when(masqueradeHandler.stop(isNull(), anyString()))
//                        .thenThrow(new UnauthorizedStatusException());
//            }
//
//            default -> throw new IllegalArgumentException("No arrangement for " + scenario.name());
//        }
//    }



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
