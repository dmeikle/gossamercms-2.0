package com.gossamercms.mvc.http;

/**
 * Helper to create ApiResponse objects used by module handlers.
 */
public class ResponseBuilder {

	public static <T> ApiResponse<T> ok(T data) {
		return ApiResponse.ok(data);
	}

	public static ApiResponse<Void> message(String msg) {
		return ApiResponse.message(msg);
	}

	public static ApiResponse<Void> badRequest(String msg) {
		ApiResponse<Void> r = new ApiResponse<>();
		r.setSuccess(false);
		r.setMessage(msg);
		return r;
	}

	public static ApiResponse<Void> unauthorized(String msg) {
		ApiResponse<Void> r = new ApiResponse<>();
		r.setSuccess(false);
		r.setMessage(msg);
		return r;
	}

	public static ApiResponse<Void> notFound(String msg) {
		ApiResponse<Void> r = new ApiResponse<>();
		r.setSuccess(false);
		r.setMessage(msg);
		return r;
	}
}
