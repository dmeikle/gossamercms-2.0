package com.gossamercms.notifications.services;

import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.notifications.data.NotificationJobsDbService;
import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.dtos.requests.NotificationRequest;
import com.gossamercms.notifications.models.NotificationJobStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@ModuleService
@RequiredArgsConstructor
public class NotificationJobService {

    private final NotificationJobsDbService dbService;

    @Transactional
    public NotificationJobDto createReminderJob(NotificationRequest request) {
        NotificationJobDto job = NotificationJobDto.builder()
                .recipientId(request.getRecipientId())
                .recipientType(request.getRecipientType())
                .recipientContactId(request.getRecipientContactId())
                .notificationType(request.getNotificationType())
                .notificationChannel(request.getNotificationChannel())
                .title(request.getTitle())
                .body(request.getBody())
                .priority(request.getPriority())
                .metadata(request.getMetadata())
                .scheduledAt(Instant.now())
                .status(NotificationJobStatus.PENDING)
                .attemptCount(0)
                .build();

        return dbService.create(request.getCreatedBy(), job);
    }
}
