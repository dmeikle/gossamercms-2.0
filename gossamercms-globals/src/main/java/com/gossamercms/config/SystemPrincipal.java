package com.gossamercms.config;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SystemPrincipal {

    private final String uuid;

    public SystemPrincipal(Environment env) {
        // property: gossamercms.system.uuid
        this.uuid = env.getProperty("gossamercms.system.uuid", "11111111-2222-3333-4444-555555555555");
    }

    public String getUuid() {
        return uuid;
    }
}
