package com.gossamercms.outbox.data;



import com.gossamercms.outbox.models.OutboxEvent;

import java.util.List;
import java.util.UUID;

public interface OutboxRepositoryInterface {

    List<OutboxEvent> fetchPending(int limit);

    void markProcessing(UUID id);

    void markSent(UUID id);

    void markFailed(UUID id, String error);

    void create(OutboxEvent event);
}