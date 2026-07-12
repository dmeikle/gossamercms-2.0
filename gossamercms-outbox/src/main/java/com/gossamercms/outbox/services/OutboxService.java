package com.gossamercms.outbox.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gossamercms.outbox.data.OutboxDbService;
import com.gossamercms.outbox.data.OutboxRepository;
import com.gossamercms.outbox.eventbus.DomainEvent;
import com.gossamercms.outbox.models.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxService {

    private final ObjectMapper mapper;
    private final OutboxDbService dbService;

    public void publish(String aggregateType,
                        UUID aggregateId,
                        DomainEvent event) {

        try {
            UUID correlationId = Optional.ofNullable(MDC.get("correlationId"))
                    .map(UUID::fromString)
                    .orElse(UUID.randomUUID());

            OutboxEvent outbox = OutboxEvent.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(aggregateType)
                    .aggregateId(aggregateId)
                    .eventType(event.getClass().getName())
                    .payload(mapper.writeValueAsString(event))
                    .status(OutboxRepository.OutboxStatus.PENDING.name())
                    .retryCount(0)
                    .createdAt(Instant.now())
                    .correlationId(correlationId)
                    .build();

            //repository.create(outbox);
            dbService.create(outbox.getCreatedBy(), outbox.toDto());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException(
                    "Failed to serialize event: " + event.getClass().getName(), e);
        }
    }
}
