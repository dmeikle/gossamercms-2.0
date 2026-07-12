package com.gossamercms.outbox.eventbus;


import com.gossamercms.outbox.dispatcher.GossamerEventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EventRegistry {

    private final Map<Class<?>, List<GossamerEventListener<?>>> listeners =
            new ConcurrentHashMap<>();

    public void register(GossamerEventListener<?> listener) {
        listeners
                .computeIfAbsent(listener.eventType(),
                        k -> new CopyOnWriteArrayList<>())
                .add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T> List<GossamerEventListener<DomainEvent>> getListeners(Class<T> eventType) {
        return (List<GossamerEventListener<DomainEvent>>) (List<?>) listeners.getOrDefault(
                eventType,
                List.of());
    }
}
