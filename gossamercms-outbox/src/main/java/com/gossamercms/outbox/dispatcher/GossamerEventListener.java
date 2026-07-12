package com.gossamercms.outbox.dispatcher;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public interface GossamerEventListener<T> extends ApplicationListener<ApplicationEvent> {

    Class<T> eventType();

    void handle(T event);

    @Override
    default void onApplicationEvent(ApplicationEvent event) {
        Object payload = event;
        // If Spring wrapped a payload into PayloadApplicationEvent, unwrap it
        if (event instanceof org.springframework.context.PayloadApplicationEvent) {
            payload = ((org.springframework.context.PayloadApplicationEvent<?>) event).getPayload();
        }
        if (eventType().isInstance(payload)) {
            handle(eventType().cast(payload));
        }
    }
}
