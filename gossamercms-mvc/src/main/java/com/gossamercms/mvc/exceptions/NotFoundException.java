package com.gossamercms.mvc.exceptions;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NotFoundException extends ApiException{

    public NotFoundException(UUID id) {
        super(MvcExceptionCodes.NOT_FOUND, "No resource found with id " + id, HttpStatus.NOT_FOUND.value());
    }
}

