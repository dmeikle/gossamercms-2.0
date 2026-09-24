package com.gossamercms.notifications.models;

import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationJob implements BaseModel{
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

    public NotificationJobDto toDto() {
        return NotificationJobDto.builder()
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


public static final ModelMeta META = ModelMeta.builderWithId("notification_jobs")
    .column("id", UUID.class)
    .column("recipientId", UUID.class)
    .column("recipientType", String.class)
    .column("recipientContactId", UUID.class)
    .column("notificationType", String.class)
    .column("notificationChannel", String.class)
    .column("title", String.class)
    .column("body", String.class)
    .column("priority", String.class)
    .column("metadata", String.class)
    .column("scheduledAt", Instant.class)
    .column("status", String.class)
    .column("attemptCount", Integer.class)
    .column("nextAttemptAt", Instant.class)
    .column("claimedAt", Instant.class)
    .column("sentAt", Instant.class)
    .column("lastError", String.class)
    .column("createdAt", Instant.class)
    .column("updatedAt", Instant.class)
    .defaultSort("id desc")
    .build();



    @Override
    public ModelMeta meta() {
        return META;
    }

    @Override
    public ModelMeta metaOf() {
        return BaseModel.super.metaOf();
    }

}
