package com.gossamercms.outbox.dtos;

import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.helpers.annotations.JsonColumn;
import com.gossamercms.outbox.models.OutboxEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class OutboxEventDto implements DtoWithId {
    private UUID id;

    private String aggregateType;   // e.g. PATIENT, APPOINTMENT
    private UUID aggregateId;

    private String eventType;        // e.g. CREATED, UPDATED

    @JsonColumn
    private String payload;          // JSON string

    private String status;           // PENDING, PROCESSING, SENT, FAILED

    private int retryCount;

    private String errorMessage;

    private Instant createdAt;

    private Instant processedAt;

    private UUID correlationId;

    private UUID createdBy;

    public OutboxEvent toEntity() {
        return OutboxEvent.builder()
                .id(id)
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .payload(payload)
                .status(status)
                .retryCount(retryCount)
                .errorMessage(errorMessage)
                .createdAt(createdAt)
                .processedAt(processedAt)
                .correlationId(correlationId)
                .build();
    }
}
