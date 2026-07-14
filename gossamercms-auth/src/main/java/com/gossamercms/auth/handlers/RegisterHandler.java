package com.gossamercms.auth.handlers;

import com.gossamercms.auth.adapters.AuthenticationProvider;
import com.gossamercms.auth.data.LoginIdentityDbService;
import com.gossamercms.auth.data.RolesDbService;
import com.gossamercms.auth.dtos.LoginIdentityDto;
import com.gossamercms.auth.dtos.RoleDto;
import com.gossamercms.auth.dtos.requests.AdminRegisterRequestDto;
import com.gossamercms.auth.dtos.requests.RegisterRequestDto;
import com.gossamercms.auth.dtos.responses.RegisterResponseDto;
import com.gossamercms.auth.factories.LoginIdentityFactory;
import com.gossamercms.auth.factories.RoleClaimsFactory;
import com.gossamercms.auth.models.LoginIdentity;
import com.gossamercms.mvc.annotations.ModuleHandler;
import com.gossamercms.security.services.JwtService;
import com.gossamercms.security.shared.factories.JwtClaimsFactory;
import com.gossamercms.users.api.*;
import com.gossamercms.users.config.UserContextDefaults;
import com.gossamercms.users.data.*;
import com.gossamercms.users.domain.*;
import com.gossamercms.users.exceptions.LoginAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@ModuleHandler
public class RegisterHandler {

    private final UsersDbService usersDb;
    private final LoginIdentityDbService identityDb;
    private final UserAddressDbService addressDb;
    private final UserTelephonesDbService telephonesDb;
    private final UserContextsDbService contextsDb;
    private final AccountMappingsDbService accountMappingsDbService;
    private final JwtService jwtService;
    private final AuthenticationProvider authProvider;
    private final RoleClaimsFactory roleClaimsFactory;

    private final String DEFAULT_TYPE = "default";
    private static final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    public RegisterHandler(
            UsersDbService usersDb,
            LoginIdentityDbService identityDb,
            UserAddressDbService addressDb,
            UserTelephonesDbService telephonesDb,
            UserContextsDbService contextsDb,
            AccountMappingsDbService accountMappingsDbService,
            RolesDbService rolesDbService,
            RoleClaimsFactory roleClaimsFactory,
            JwtService jwtService,
            AuthenticationProvider authProvider
    ) {
        this.usersDb = usersDb;
        this.identityDb = identityDb;
        this.addressDb = addressDb;
        this.telephonesDb = telephonesDb;
        this.contextsDb = contextsDb;
        this.accountMappingsDbService = accountMappingsDbService;
        this.roleClaimsFactory = roleClaimsFactory;
        this.jwtService = jwtService;
        this.authProvider = authProvider;
    }

    public RegisterResponseDto handle(AdminRegisterRequestDto req, RoleDto role) throws LoginAlreadyExistsException {

        return handle(RegisterRequestDto.builder()
                .lastname(req.getLastname())
                .firstname(req.getFirstname())
                .addresses(req.getAddresses())
                .telephones(req.getTelephones())
                .email(req.getEmail())
                .password(req.getPassword())
                .marketingOptIn(req.getMarketingOptIn())
                .telephones(req.getTelephones())
                .userContext(req.getUserContext())
                .build(), role, ""); //TODO: until we test this, it may generate a new token for the admin by accident
    }

    public RegisterResponseDto handle(RegisterRequestDto req, RoleDto role, String sessionId) throws LoginAlreadyExistsException {

        //first check to see if the email already exists
        if(authProvider.emailExists(req.getEmail())) {
            throw new LoginAlreadyExistsException();
        }
        // 1. Create user record
        User user = User.builder()
                .id(null)
                .firstname(req.getFirstname())
                .lastname(req.getLastname())
                .status("ACTIVE")
                .createdOn(null)
                .build();


        UserDto savedUser = usersDb.create(null, user.toDto());

        // 2. Create external identity (Auth0, DB, LDAP, etc.)
        String providerUserId = authProvider.register(req.getEmail(), req.getPassword());

        // 3. Store login identity
        LoginIdentity identity = LoginIdentityFactory.createAuth0EmailIdentity(
                savedUser.getId(),
                req.getEmail(),
                providerUserId
        );

        LoginIdentityDto savedIdentity = identityDb.create(
                savedUser.getId(),
                identity.toDto()
        );

        // 4. Create addresses
        List<AddressDto> savedAddresses = new ArrayList<>();
        if (req.getAddresses() != null) {
            for (AddressDto a : req.getAddresses()) {
                Address addr = Address.builder()
                        .id(null)
                        .userId(savedUser.getId())
                        .address1(a.getAddress1())
                        .address2(a.getAddress2())
                        .city(a.getCity())
                        .stateProvince(a.getStateProvince())
                        .postalCode(a.getPostalCode())
                        .countryCode(a.getCountryCode())
                        .type(a.getType())
                        .isDefault(a.isDefault())
                        .build();

                AddressDto saved = addressDb.create(savedUser.getId(), addr.toDto());
                savedAddresses.add(saved);
            }
        }

        // 5. Create telephones
        List<UserTelephoneDto> savedTelephones = new ArrayList<>();
        if (req.getTelephones() != null) {
            for (UserTelephoneDto t : req.getTelephones()) {
                UserTelephone tel = UserTelephone.builder()
                        .id(null)
                        .userId(savedUser.getId())
                        .countryCode(t.getCountryCode())
                        .numberRaw(t.getNumberRaw())
                        .numberE164(null)
                        .type(t.getType())
                        .verified(false)
                        .smsOptIn(t.isSmsOptIn())
                        .preferred(t.isPreferred())
                        .extension(t.getExtension())
                        .createdOn(null)
                        .build();

                UserTelephoneDto saved = telephonesDb.create(savedUser.getId(), tel.toDto());
                savedTelephones.add(saved);
            }
        }

        //7. Generate UserContext
        UserContextDto userContext = contextsDb.create(savedUser.getId(),
                UserContext.builder()
                .userId(savedUser.getId())
                .createdAt(null)
                .roleId(role.getId())
                .metadata(UserContextDefaults.forType(req.getUserContext().getContextType()))
                .contextType(req.getUserContext().getContextType())
                .isDefault((true)) //it is assumed
                .build().toDto()
                        );


        String [] permissionNames = roleClaimsFactory.getPermissionsByUserContext(userContext.getId());

        // 8. Generate JWT
        String token = jwtService.generateToken(
                savedUser.getId(),
                JwtClaimsFactory.toClaims(
                        savedUser.getId(),
                        userContext.getId(),
                        sessionId,
                        identity.getIdentifier(),
                        role.getName(),
                        permissionNames
                )
        );

        //9. Generate AccountMapping
        AccountMappingDto accountMapping = accountMappingsDbService.create(savedUser.getId(),
                AccountMapping.builder()
                        .userContextId(userContext.getId())
                        .isDefault(Boolean.TRUE)
                        .roleId(role.getId())
                        .build().toDto()
                );

        // 10. Return response
        return new RegisterResponseDto(
                savedUser.getId(),
                savedIdentity.getIdentifier(),
                savedUser.getFirstname(),
                savedUser.getLastname(),
                savedAddresses,
                savedTelephones,
                userContext,
                token
        );
    }
}