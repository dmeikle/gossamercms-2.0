package com.gossamercms.outbox.dispatcher;

import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.helpers.JsonbHelper;
import com.gossamercms.outbox.data.OutboxDbService;
import com.gossamercms.outbox.dtos.OutboxEventDto;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service

public class OutboxDispatcher {


    private final ApplicationEventPublisher publisher;
    private final OutboxDbService dbService;

    public OutboxDispatcher(OutboxDbService dbService, ApplicationEventPublisher publisher) {
        this.dbService = dbService;
        this.publisher = publisher;
        System.out.println("************************* OutboxDispatcher constructed **************************");
    }

    @Scheduled(fixedDelay = 30000) // every second
    public void dispatch() {
        ListResultset<OutboxEventDto> events = (ListResultset<OutboxEventDto>) dbService.getPendingEvents();
        System.out.println("OutboxDispatcher dispatching events " + events.list().size() + " events found");
        for (OutboxEventDto row : events.list()) {
            try {
                Class<?> clazz = Class.forName(row.getEventType());
                Object payload = JsonbHelper.fromJsonb(row.getPayload(), clazz);
                publisher.publishEvent(payload);

                dbService.updateById(row.getCreatedBy(),
                        OutboxEventDto.builder().status("SENT")
                                .build(), row.getId());
            } catch (Exception e) {
                e.printStackTrace();
                dbService.updateById(row.getCreatedBy(),
                        OutboxEventDto.builder().status("FAILED")
                        .retryCount(row.getRetryCount() + 1)
                        .build(), row.getId());
            }
        }
    }
}

