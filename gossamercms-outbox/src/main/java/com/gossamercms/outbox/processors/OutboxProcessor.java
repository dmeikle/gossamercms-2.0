package com.gossamercms.outbox.processors;

import com.gossamercms.outbox.models.OutboxEvent;

import java.util.List;

import com.gossamercms.outbox.data.OutboxRepository;
import com.gossamercms.outbox.dispatcher.OutboxEventDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
//@RequiredArgsConstructor
public class OutboxProcessor {

//    private final OutboxRepository repository;
//    private final OutboxEventDispatcher dispatcher;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void process() {

//        List<OutboxEvent> events = repository.fetchPending(50);
//
//        for (OutboxEvent event : events) {
//            try {
//                repository.markProcessing(event.getId());
//
//                dispatcher.dispatch(event);
//
//                repository.markSent(event.getId());
//
//            } catch (Exception e) {
//                repository.markFailed(event.getId(), e.getMessage());
//            }
//        }
    }
}
