package com.gossamercms.notifications.dtos;

import com.gossamercms.notifications.models.NotificationJob;
import com.gossamercms.mvc.data.DtoWithId;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJobDto implements DtoWithId{
    private UUID id;
    private UUID recipientId;
    private String recipientType;
    private UUID recipientContactId;
    private String notificationType;
    private String notificationChannel;
    private String title;
    private String body;
    private String priority;
    private String metadata;
    private Instant scheduledAt;
    private String status;
    private Integer attemptCount;
    private Instant nextAttemptAt;
    private Instant claimedAt;
    private Instant sentAt;
    private String lastError;
    private Instant createdAt;
    private Instant updatedAt;

    public NotificationJob toEntity() {
        return NotificationJob.builder()
            .id(id)
            .recipientId(recipientId)
            .recipientType(recipientType)
            .recipientContactId(recipientContactId)
            .notificationType(notificationType)
            .notificationChannel(notificationChannel)
            .title(title)
            .body(body)
            .priority(priority)
            .metadata(metadata)
            .scheduledAt(scheduledAt)
            .status(status)
            .attemptCount(attemptCount)
            .nextAttemptAt(nextAttemptAt)
            .claimedAt(claimedAt)
            .sentAt(sentAt)
            .lastError(lastError)
            .createdAt(createdAt)
            .updatedAt(updatedAt)
            .build();
    }
}
