package com.gossamercms.mvc.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedStatusException extends ApiException{
    public UnauthorizedStatusException() {
        super(MvcExceptionCodes.UNAUTHORIZED_STATUS, "User is not logged in", HttpStatus.UNAUTHORIZED.value());
    }
}
