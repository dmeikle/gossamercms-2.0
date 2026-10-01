package com.gossamercms.auth.exceptions;

import com.gossamercms.auth.config.ExceptionCodes;
import com.gossamercms.mvc.exceptions.ApiException;
import org.springframework.http.HttpStatus;

public class AuthProviderLoginNotFoundException extends ApiException {
    public AuthProviderLoginNotFoundException(String email) {
        super(ExceptionCodes.AUTH_PROVIDER_LOGIN_NOT_FOUND, "No auth account found for " + email, HttpStatus.NOT_FOUND.value());
    }
}
