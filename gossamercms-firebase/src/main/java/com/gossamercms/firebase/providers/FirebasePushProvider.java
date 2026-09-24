package com.gossamercms.firebase.providers;

import com.google.firebase.messaging.*;
import com.gossamercms.notifications.providers.PushNotificationProvider;
import com.gossamercms.notifications.providers.PushSendResult;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Firebase implementation of PushNotificationProvider.
 * Provides high-priority push notifications to Android and iOS devices.
 */
@Service
public class FirebasePushProvider implements PushNotificationProvider {

    @Override
    public PushSendResult send(
            String deviceToken,
            String title,
            String body,
            Map<String, String> data) {

        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .build()
                )
                .setApnsConfig(
                        ApnsConfig.builder()
                                .setAps(
                                        Aps.builder()
                                                .setSound("default")
                                                .build()
                                )
                                .build()
                )
                .build();

        try {
            String messageId = FirebaseMessaging.getInstance().send(message);
            return PushSendResult.accepted(messageId);
        } catch (FirebaseMessagingException e) {
            return PushSendResult.failed(
                    e.getMessagingErrorCode().toString(),
                    e.getMessage()
            );
        }
    }
}
