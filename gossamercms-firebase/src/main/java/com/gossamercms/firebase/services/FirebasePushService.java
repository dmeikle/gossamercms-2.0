package com.gossamercms.firebase.services;

import com.google.firebase.messaging.*;
import com.gossamercms.firebase.dtos.results.FirebaseSendResult;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FirebasePushService {

    public FirebaseSendResult send(
            String firebaseToken,
            String title,
            String body,
            Map<String, String> data) {

        Message message = Message.builder()
                .setToken(firebaseToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putAllData(data)
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(
                                        AndroidConfig.Priority.HIGH
                                )
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
            String messageId =
                    FirebaseMessaging.getInstance()
                            .send(message);

            return FirebaseSendResult.accepted(messageId);

        } catch (FirebaseMessagingException e) {

            return FirebaseSendResult.failed(
                    e.getMessagingErrorCode().toString(),
                    e.getMessage()
            );
        }
    }
}
