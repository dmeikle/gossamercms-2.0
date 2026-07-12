package com.gossamercms.outbox.config;

import com.gossamercms.outbox.dispatcher.GossamerEventListener;
import com.gossamercms.outbox.eventbus.EventRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventBusInitializer {

    public EventBusInitializer(
            List<GossamerEventListener<?>> listeners,
            EventRegistry registry) {

        listeners.forEach(registry::register);
    }
}
