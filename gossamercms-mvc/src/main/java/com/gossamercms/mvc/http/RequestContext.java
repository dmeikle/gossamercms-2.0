package com.gossamercms.mvc.http;

import java.util.Map;
import java.util.UUID;

/**
 * Lightweight holder for data passed to module-style handlers.
 *
 * Only implements the small surface required by handlers in this
 * repository: a userId accessor and a helper to read UUID values
 * from the parsed request body.
 */
public class RequestContext {

	private final UUID userId;
	private final String identifier;
	private final String sessionId;
	private final UUID correlationId;
	private final Map<String, Object> body;

	public RequestContext(UUID userId, Map<String, Object> body) {
		this(userId, null, null, UUID.randomUUID(), body);
	}

	public RequestContext(UUID userId, String identifier, Map<String, Object> body) {
		this(userId, identifier, null, UUID.randomUUID(), body);
	}

	public RequestContext(UUID userId, String identifier, String sessionId, Map<String, Object> body) {
		this(userId, identifier, sessionId, UUID.randomUUID(), body);
	}

	public RequestContext(UUID userId, String identifier, String sessionId, UUID correlationId, Map<String, Object> body) {
		this.userId = userId;
		this.identifier = identifier;
		this.sessionId = sessionId;
		this.correlationId = correlationId;
		this.body = body;
	}

	public UUID userId() {
		return userId;
	}

	public String identifier() {
		return identifier;
	}

	public String sessionId() {
		return sessionId;
	}

	public UUID correlationId() {
		return correlationId;
	}

	public Map<String, Object> body() {
		return body;
	}
	
	/**
	 * Read a UUID value from the request body. Accepts either a UUID
	 * instance or a String representation (then parsed with UUID.fromString).
	 * Returns null if the key is missing or cannot be parsed.
	 */
	public UUID bodyAsUuid(String key) {
		if (body == null) return null;
		Object v = body.get(key);
		if (v == null) return null;
		if (v instanceof UUID u) return u;
		if (v instanceof String s) {
			try {
				return UUID.fromString(s);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}

}
