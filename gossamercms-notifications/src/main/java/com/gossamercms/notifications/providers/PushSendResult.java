package com.gossamercms.notifications.providers;

/**
 * Result of a push notification send attempt.
 * Agnostic to the underlying provider implementation.
 */
public record PushSendResult(
    boolean accepted,
    String messageId,
    String errorCode,
    String errorMessage
) {
    public static PushSendResult accepted(String messageId) {
        return new PushSendResult(true, messageId, null, null);
    }

    public static PushSendResult failed(String code, String message) {
        return new PushSendResult(false, null, code, message);
    }
}
