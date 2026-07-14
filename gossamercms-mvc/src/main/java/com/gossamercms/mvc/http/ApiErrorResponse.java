package com.gossamercms.mvc.http;

public record ApiErrorResponse(
        boolean error,
        int status,
        String errorCode,
        String message
) {}