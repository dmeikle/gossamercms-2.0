package com.gossamercms.outbox.dispatcher;

import com.gossamercms.outbox.models.OutboxEvent;

public interface OutboxEventDispatcher {
    void dispatch(OutboxEvent event) throws Exception;
}