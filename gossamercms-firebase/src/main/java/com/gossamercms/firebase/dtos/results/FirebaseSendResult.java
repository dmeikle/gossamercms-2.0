package com.gossamercms.firebase.dtos.results;

public record FirebaseSendResult(
        boolean accepted,
        String messageId,
        String errorCode,
        String errorMessage
) {

    public static FirebaseSendResult accepted(
            String messageId) {
        return new FirebaseSendResult(
                true, messageId, null, null);
    }

    public static FirebaseSendResult failed(
            String code,
            String message) {
        return new FirebaseSendResult(
                false, null, code, message);
    }
}