package com.gossamercms.outbox.eventbus;


public interface EventBus {
    void publish(DomainEvent event);
}
