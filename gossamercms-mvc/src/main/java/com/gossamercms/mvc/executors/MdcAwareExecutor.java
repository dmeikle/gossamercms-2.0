package com.gossamercms.mvc.executors;

import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Executor;
import org.springframework.core.task.TaskDecorator;


@RequiredArgsConstructor
public class MdcAwareExecutor implements Executor {
    private final Executor delegate;

    public void execute(Runnable r) {
        Map<String,String> context = MDC.getCopyOfContextMap();
        delegate.execute(() -> {
            if (context != null) MDC.setContextMap(context);
            try { r.run(); } finally { MDC.clear(); }
        });
    }
}

