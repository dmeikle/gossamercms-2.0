package com.gossamercms.mvc.handlers;

import com.gossamercms.mvc.exceptions.ApiException;
import com.gossamercms.mvc.http.ApiErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                true,
                ex.getStatus(),
                ex.getCode(),
                ex.getMessage()
        );

        return ResponseEntity
                .status(ex.getStatus())
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception ex) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String errorCode = "INTERNAL_ERROR";
        String message = "An unexpected error occurred.";

        if (ex instanceof ErrorResponse errorResponse) {
            status = HttpStatus.valueOf(errorResponse.getStatusCode().value());
            errorCode = status.name();
            message = ex.getMessage();
        }

        ApiErrorResponse response = new ApiErrorResponse(
                true,
                status.value(),
                errorCode,
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}
