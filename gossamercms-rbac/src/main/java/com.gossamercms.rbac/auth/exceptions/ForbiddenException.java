package com.gossamercms.rbac.auth.exceptions;

import com.gossamercms.mvc.exceptions.ApiException;
import com.gossamercms.rbac.config.ExceptionCodes;
import org.springframework.http.HttpStatus;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(ExceptionCodes.INVALID_ROLE, message, HttpStatus.FORBIDDEN.value());
    }
}
