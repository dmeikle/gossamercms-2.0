package com.gossamercms.mvc.http;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data = data;
        return r;
    }

    public static ApiResponse<Void> message(String msg) {
        ApiResponse<Void> r = new ApiResponse<>();
        r.success = true;
        r.message = msg;
        return r;
    }
}
