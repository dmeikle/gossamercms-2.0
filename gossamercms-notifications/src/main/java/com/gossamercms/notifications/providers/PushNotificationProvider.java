package com.gossamercms.notifications.providers;

import java.util.Map;

/**
 * Generic push notification provider abstraction.
 * Allows swapping between Firebase, AWS SNS, OneSignal, etc. without changing dispatcher code.
 */
public interface PushNotificationProvider {
    
    /**
     * Send a push notification to a device token.
     * 
     * @param deviceToken Device-specific token (Firebase token, SNS endpoint, etc.)
     * @param title Notification title
     * @param body Notification body/message
     * @param data Key-value metadata to include
     * @return Result containing success status and message ID or error details
     */
    PushSendResult send(
        String deviceToken,
        String title,
        String body,
        Map<String, String> data
    );
}
