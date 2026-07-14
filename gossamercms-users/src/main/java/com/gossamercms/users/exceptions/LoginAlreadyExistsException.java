package com.gossamercms.users.exceptions;

import com.gossamercms.mvc.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public class LoginAlreadyExistsException extends ApiException {
    public LoginAlreadyExistsException() {
        super(UserExceptionCodes.EMAIL_ALREADY_EXISTS, "Login already exists", HttpStatus.CONFLICT.value());
    }
}
