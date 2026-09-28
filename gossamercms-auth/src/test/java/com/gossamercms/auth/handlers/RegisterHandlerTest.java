package com.gossamercms.auth.handlers;

import com.gossamercms.auth.adapters.AuthenticationProvider;
import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.data.RolesDbService;
import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.dtos.requests.RegisterRequestDto;
import com.gossamercms.auth.factories.RoleClaimsFactory;
import com.gossamercms.security.services.JwtService;
import com.gossamercms.users.api.AccountMappingDto;
import com.gossamercms.users.api.UserContextDto;
import com.gossamercms.users.api.UserDto;
import com.gossamercms.users.data.AccountMappingsDbService;
import com.gossamercms.users.data.UserAddressDbService;
import com.gossamercms.users.data.UserContextsDbService;
import com.gossamercms.users.data.UserTelephonesDbService;
import com.gossamercms.users.data.UsersDbService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RegisterHandlerTest {

    private final UsersDbService usersDb = mock(UsersDbService.class);
    private final LoginIdentityDbService identityDb = mock(LoginIdentityDbService.class);
    private final UserAddressDbService addressDb = mock(UserAddressDbService.class);
    private final UserTelephonesDbService telephonesDb = mock(UserTelephonesDbService.class);
    private final UserContextsDbService contextsDb = mock(UserContextsDbService.class);
    private final AccountMappingsDbService accountMappingsDbService = mock(AccountMappingsDbService.class);
    private final RolesDbService rolesDbService = mock(RolesDbService.class);
    private final RoleClaimsFactory roleClaimsFactory = mock(RoleClaimsFactory.class);
    private final JwtService jwtService = mock(JwtService.class);
    private final AuthenticationProvider authProvider = mock(AuthenticationProvider.class);

    private RegisterHandler handler;

    @BeforeEach
    void setUp() {
        handler = new RegisterHandler(
                usersDb,
                identityDb,
                addressDb,
                telephonesDb,
                contextsDb,
                accountMappingsDbService,
                rolesDbService,
                roleClaimsFactory,
                jwtService,
                authProvider
        );

        UUID userId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        UUID contextId = UUID.fromString("22222222-2222-2222-2222-222222222222");

        when(authProvider.emailExists(anyString())).thenReturn(false);
        when(authProvider.register(anyString(), anyString())).thenReturn("provider|123");
        when(usersDb.create(eq(null), any())).thenReturn(UserDto.builder()
                .id(userId)
                .firstname("Alice")
                .lastname("Example")
                .build());
        when(identityDb.create(eq(userId), any())).thenReturn(LoginIdentityDto.builder()
                .id(UUID.fromString("33333333-3333-3333-3333-333333333333"))
                .userId(userId)
                .identifier("alice@example.com")
                .build());
        when(contextsDb.create(eq(userId), any())).thenAnswer(invocation -> {
            UserContextDto dto = invocation.getArgument(1);
            return dto.toBuilder().id(contextId).build();
        });
        when(roleClaimsFactory.getPermissionsByUserContext(contextId)).thenReturn(new String[0]);
        when(jwtService.generateToken(eq(userId), any())).thenReturn("jwt-token");
        when(accountMappingsDbService.create(eq(userId), any())).thenReturn(AccountMappingDto.builder()
                .id(UUID.fromString("44444444-4444-4444-4444-444444444444"))
                .userContextId(contextId)
                .build());
    }

    @Test
    void handleMergesDefaultMetadataIntoProvidedUserContext() throws Exception {
        RoleDto role = RoleDto.builder().id(UUID.fromString("55555555-5555-5555-5555-555555555555")).name("member").build();
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("alice@example.com")
                .password("secret")
                .firstname("Alice")
                .lastname("Example")
                .userContext(UserContextDto.builder()
                        .contextType("default")
                        .metadata(Map.of("patientId", "P-123"))
                        .build())
                .build();

        handler.handle(request, role, "session-1");

        ArgumentCaptor<UserContextDto> captor = ArgumentCaptor.forClass(UserContextDto.class);
        verify(contextsDb).create(any(), captor.capture());
        Map<String, Object> metadata = captor.getValue().getMetadata();
        assertEquals("P-123", metadata.get("patientId"));
        assertEquals(true, metadata.get("defaultContext"));
        assertEquals("/members/dashboard", metadata.get("homepage"));
    }

    @Test
    void handleUsesDefaultUserContextWhenRequestOmitsIt() throws Exception {
        RoleDto role = RoleDto.builder().id(UUID.fromString("66666666-6666-6666-6666-666666666666")).name("member").build();
        RegisterRequestDto request = RegisterRequestDto.builder()
                .email("bob@example.com")
                .password("secret")
                .firstname("Bob")
                .lastname("Example")
                .userContext(null)
                .build();

        handler.handle(request, role, "session-2");

        ArgumentCaptor<UserContextDto> captor = ArgumentCaptor.forClass(UserContextDto.class);
        verify(contextsDb).create(any(), captor.capture());
        assertEquals("default", captor.getValue().getContextType());
        assertTrue(Boolean.TRUE.equals(captor.getValue().getMetadata().get("defaultContext")));
    }
}
