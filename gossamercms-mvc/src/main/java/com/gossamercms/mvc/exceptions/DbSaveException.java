package com.gossamercms.mvc.exceptions;

import com.gossamercms.mvc.config.ExceptionCodes;
import org.springframework.http.HttpStatus;

public class DbSaveException extends ApiException {
    public DbSaveException(String message) {
        super(ExceptionCodes.SAVE_DATA_EXCEPTION, message, HttpStatus.BAD_REQUEST.value());
    }
}
