package com.gossamercms.notifications.dtos.requests;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NotificationRequest {
    UUID createdBy;
    UUID recipientId;
    String recipientType;  // USER, PATIENT, ADMIN
    UUID recipientContactId;
    String notificationType;
    String notificationChannel;  // SMS, EMAIL, PUSH
    String title;
    String body;
    String priority;  // HIGH, NORMAL, LOW
    String metadata;
}
