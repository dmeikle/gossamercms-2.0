package com.gossamercms.notifications.services;

import com.gossamercms.notifications.providers.PushNotificationProvider;
import com.gossamercms.notifications.providers.PushSendResult;
import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.notifications.data.NotificationJobsDbService;
import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.models.NotificationJobStatus;
import com.gossamercms.users.api.UserDeviceDto;
import com.gossamercms.users.data.UserDevicesDbService;
import gossamercms.globals.config.SystemPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;

@ModuleService
@RequiredArgsConstructor
public class NotificationDispatcherService {

    private final NotificationJobsDbService notificationJobsDbService;
    private final UserDevicesDbService devicesDbService;
    private final PushNotificationProvider pushProvider;
   // private final NotificationDeliveryDbService notificationDeliveryDbService;

//    private final NotificationJobRepository jobRepository;
//    private final PatientDeviceRepository deviceRepository;
//    private final NotificationDeliveryRepository deliveryRepository;

    @Scheduled(fixedDelay = 5000)
    public void dispatch() {

        List<NotificationJobDto> jobs =
                claimDueJobs(50);

        for (NotificationJobDto job : jobs) {
            processJob(job);
        }
    }

    private void processJob(NotificationJobDto job) {

        List<UserDeviceDto> devices =
                devicesDbService.getAll(QueryOptions.builder()
                        .filters(Map.of("userId", job.getRecipientId()))
                        .build()).list();


        for (UserDeviceDto device : devices) {

            PushSendResult result =
                    pushProvider.send(
                            device.getFirebaseToken(),
                            job.getTitle(),
                            job.getBody(),
                            Map.of(
                                    "notificationJobId",
                                    job.getId().toString(),
                                    "notificationType",
                                    job.getNotificationType(),
                                    "metadata",
                                    job.getMetadata()
                            )
                    );

            recordDelivery(job, device, result);
        }

        markJobComplete(job);
    }

    private List<NotificationJobDto> claimDueJobs(int limit) {
        // Implement atomic claim transaction.
        return notificationJobsDbService.getAll(QueryOptions.builder()
                .filters(Map.of("status", NotificationJobStatus.PENDING))
                .size(limit).build()).list();
    }

    private void recordDelivery(
            NotificationJobDto job,
            UserDeviceDto device,
            PushSendResult result) {
        // Persist attempt and Firebase result.
    }

    private void markJobComplete(NotificationJobDto job) {
       notificationJobsDbService.update(SystemPrincipal.ID,
               NotificationJobDto.builder()
                       .status(NotificationJobStatus.SENT).build(),
                               Map.of("id", job.getId())
                       );
    }
}
//TODO: Add stale-claim recovery
//
//If a server crashes after claiming a job, it must become available again.
//
//Run a recovery query periodically:
//
//UPDATE notification_jobs
//SET status = 'PENDING',
//    claimed_at = NULL,
//    next_attempt_at = NOW()
//WHERE status = 'PROCESSING'
//  AND claimed_at < NOW() - INTERVAL '10 minutes';
//
//The timeout should exceed your expected maximum processing duration.

//Exponential backoff
//public Instant calculateNextAttempt(int attempt) {
//
//    long seconds = switch (attempt) {
//        case 1 -> 30;
//        case 2 -> 120;
//        case 3 -> 600;
//        case 4 -> 1800;
//        default -> 3600;
//    };
//
//    return Instant.now().plusSeconds(seconds);
//}
//
//For clinically important reminders, don't retry forever. After a configured limit, transition to an operational or clinical escalation workflow.