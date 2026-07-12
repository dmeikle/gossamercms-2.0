package com.gossamercms.outbox.data;


import com.gossamercms.outbox.models.OutboxEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OutboxRepository implements OutboxRepositoryInterface {

    private final JdbcTemplate jdbc;

    public enum OutboxStatus {
        PENDING,
        PROCESSING,
        PROCESSED,
        FAILED
    }

    @Override
    public List<OutboxEvent> fetchPending(int limit) {

        String sql = """
            SELECT *
            FROM outbox_events
            WHERE status = '{OutboxStatus.PENDING.name()}'}'
            ORDER BY created_at
            FOR UPDATE SKIP LOCKED
            LIMIT ?
        """;

        return jdbc.query(sql, new Object[]{limit}, (rs, rowNum) ->
                OutboxEvent.builder()
                        .id(rs.getObject("id", UUID.class))
                        .aggregateType(rs.getString("aggregate_type"))
                        .aggregateId(rs.getObject("aggregate_id", UUID.class))
                        .eventType(rs.getString("event_type"))
                        .payload(rs.getString("payload"))
                        .status(rs.getString("status"))
                        .retryCount(rs.getInt("retry_count"))
                        .createdAt(rs.getTimestamp("created_at").toInstant())
                        .build()
        );
    }

    @Override
    public void markProcessing(UUID id) {
        jdbc.update("""
            UPDATE outbox_events
            SET status = 'PROCESSING'
            WHERE id = ?
        """, id);
    }

    @Override
    public void markSent(UUID id) {
        jdbc.update("""
            UPDATE outbox_events
            SET status = 'SENT',
                processed_at = now()
            WHERE id = ?
        """, id);
    }

    @Override
    public void markFailed(UUID id, String error) {
        jdbc.update("""
            UPDATE outbox_events
            SET status = 'FAILED',
                retry_count = retry_count + 1,
                error_message = ?
            WHERE id = ?
        """, error, id);
    }

    @Override
    public void create(OutboxEvent event) {

        UUID id = event.getId() != null
                ? event.getId()
                : UUID.randomUUID();

        jdbc.update("""
    INSERT INTO outbox_events (
        id,
        "aggregateType",
        "aggregateId",
        "eventType",
        payload,
        status,
        "retryCount",
        "createdAt"
    )
    VALUES (?, ?, ?, ?, ?, ?, ?, now())
    """,
                id,
                event.getAggregateType(),
                event.getAggregateId(),
                event.getEventType(),
                event.getPayload(),
                event.getStatus(),
                event.getRetryCount()
        );
    }


}
