package com.gossamercms.outbox.models;


import com.gossamercms.mvc.helpers.annotations.JsonColumn;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.outbox.dtos.OutboxEventDto;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent implements BaseModel {

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

    private UUID createdBy;

    private UUID correlationId;



    public OutboxEventDto toDto() {
        return OutboxEventDto.builder()
                .id(id)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
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

    // ⭐ Required static metadata
    public static final ModelMeta META = ModelMeta.builderWithId("outbox_events")
            .column("aggregateType", String.class, 40)
            .column("aggregateId", UUID.class, 3)
            .column("eventType", String.class, 20)
            .column("payload", String.class, 0)
            .column("status", String.class, 20)
            .column("retryCount", int.class)
            .column("errorMessage", String.class, 200)
            .column("createdAt", Instant.class)
            .column("processedAt", Instant.class)
            .column("correlationId", UUID.class)
            .defaultSort("createdAt DESC")
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
