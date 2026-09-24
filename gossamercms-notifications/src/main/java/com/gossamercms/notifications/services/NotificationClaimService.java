package com.gossamercms.notifications.services;

import com.gossamercms.mvc.annotations.ModuleService;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.notifications.data.NotificationJobsDbService;
import com.gossamercms.notifications.dtos.NotificationJobDto;
import com.gossamercms.notifications.models.NotificationJobStatus;
import gossamercms.globals.config.SystemPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@ModuleService
@RequiredArgsConstructor
public class NotificationClaimService {
    private final NotificationJobsDbService dbService;

    @Transactional
    public List<NotificationJobDto> claimJobs(int limit) {
//TODO: may need to make this iterable based on results exceeding the limit
        List<NotificationJobDto> jobs =
                dbService.getAll(QueryOptions.builder()
                        .size(limit).build()).list();

        Instant now = Instant.now();

        for (NotificationJobDto job : jobs) {
            job.setStatus(NotificationJobStatus.PROCESSING);
            job.setClaimedAt(now);
        }

        return dbService.updateAll(SystemPrincipal.ID, jobs);
    }
}
