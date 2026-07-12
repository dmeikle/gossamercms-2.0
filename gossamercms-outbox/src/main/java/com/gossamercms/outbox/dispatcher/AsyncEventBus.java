package com.gossamercms.outbox.dispatcher;

import com.gossamercms.outbox.eventbus.DomainEvent;
import com.gossamercms.outbox.eventbus.EventBus;
import com.gossamercms.outbox.eventbus.EventRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class AsyncEventBus implements EventBus {

    private final EventRegistry registry;

    private final Executor executor =
            Executors.newFixedThreadPool(8);

    @Override
    public void publish(DomainEvent event) {

        for (GossamerEventListener listener : registry.getListeners(event.getClass())) {

            executor.execute(() -> {
                try {
                    listener.handle(event);
                } catch (Exception e) {
                    // log error
                }
            });
        }
    }

}
